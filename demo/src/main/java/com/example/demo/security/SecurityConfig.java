package com.example.demo.security;

import java.util.List;

import jakarta.servlet.DispatcherType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.entities.Role;

// Configuracion de Spring Security: API sin sesion (stateless) protegida con JWT y permisos por rol
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ADMIN = Role.ADMIN;
    private static final String OPERADOR = Role.OPERADOR;
    private static final String CLIENTE = Role.CLIENTE;

    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JWTGenerator jwtGenerator;
    private final CustomUserDetailsService customUserDetailsService;

    // Paginas que pueden llamar al API (separadas por coma). En Render se cambia con la variable CORS_ORIGINS
    @Value("${app.cors.origins}")
    private String corsOrigins;

    public SecurityConfig(JwtAuthEntryPoint jwtAuthEntryPoint, JWTGenerator jwtGenerator,
            CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.jwtGenerator = jwtGenerator;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            // Para que la consola H2 se pueda ver dentro de su iframe
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // Respuestas que siguen abiertas (seguimiento en vivo): la peticion original ya se autorizo
                // con el JWT; Spring la vuelve a despachar internamente (ASYNC) para enviar cada evento
                .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()

                // ---------- Publico ----------
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/h2/**", "/error").permitAll()
                .requestMatchers(HttpMethod.POST,
                        "/api/clientes/login", "/api/operadores/login", "/api/administradores/login").permitAll()
                // Registro de clientes
                .requestMatchers(HttpMethod.POST, "/api/clientes").permitAll()
                // Menu: productos, categorias y adicionales se pueden ver sin iniciar sesion
                .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias/**", "/api/adicionales/**").permitAll()

                // ---------- Reportes de ventas (dashboard): solo ADMIN ----------
                .requestMatchers("/api/reportes/**").hasAuthority(ADMIN)

                // ---------- Administradores: solo ADMIN ----------
                .requestMatchers("/api/administradores/**").hasAuthority(ADMIN)

                // ---------- Operadores ----------
                .requestMatchers(HttpMethod.GET, "/api/operadores/**").hasAnyAuthority(ADMIN, OPERADOR)
                .requestMatchers("/api/operadores/**").hasAuthority(ADMIN)

                // ---------- Productos y categorias (crear/editar/eliminar) ----------
                .requestMatchers("/api/productos/**", "/api/categorias/**").hasAuthority(ADMIN)

                // ---------- Adicionales (crear/editar/eliminar) ----------
                .requestMatchers("/api/adicionales/**").hasAnyAuthority(ADMIN, OPERADOR)

                // ---------- Domiciliarios ----------
                .requestMatchers(HttpMethod.GET, "/api/domiciliarios/**").hasAnyAuthority(ADMIN, OPERADOR)
                .requestMatchers("/api/domiciliarios/**").hasAuthority(ADMIN)

                // ---------- Clientes ----------
                // Lista completa: admin y operador (el operador la usa en el formulario de pedido)
                .requestMatchers(HttpMethod.GET, "/api/clientes").hasAnyAuthority(ADMIN, OPERADOR)
                // Ver / editar / desactivar / eliminar un cliente: admin o el mismo cliente (se revisa en el controlador)
                .requestMatchers("/api/clientes/**").hasAnyAuthority(ADMIN, CLIENTE)

                // ---------- Carrito: solo el cliente dueno (se revisa en el controlador) ----------
                .requestMatchers("/api/carritos/**").hasAnyAuthority(CLIENTE, ADMIN)

                // ---------- Pedidos ----------
                .requestMatchers(HttpMethod.POST, "/api/pedidos").hasAnyAuthority(CLIENTE, OPERADOR, ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/pedidos/cliente/**", "/api/pedidos/*", "/api/pedidos/*/seguimiento")
                        .hasAnyAuthority(CLIENTE, OPERADOR, ADMIN)
                .requestMatchers("/api/pedidos/**").hasAnyAuthority(OPERADOR, ADMIN)
                .requestMatchers("/api/detalles-pedido/**").hasAnyAuthority(OPERADOR, ADMIN)

                // ---------- Cupones: el cliente solo prueba un codigo en su carrito; el resto es del admin ----------
                .requestMatchers(HttpMethod.GET, "/api/cupones/validar").hasAnyAuthority(CLIENTE, OPERADOR, ADMIN)
                .requestMatchers("/api/cupones/**").hasAuthority(ADMIN)

                // Cualquier otra cosa requiere estar autenticado
                .anyRequest().authenticated()
            );

        // El filtro JWT va antes del filtro de usuario/contrasena de Spring
        http.addFilterBefore(new JWTAuthenticationFilter(jwtGenerator, customUserDetailsService),
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS para que Angular (localhost:4200 o la pagina publicada) pueda mandar el header Authorization
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Patrones para aceptar tambien las URLs de vista previa de Vercel (https://*.vercel.app)
        config.setAllowedOriginPatterns(List.of(corsOrigins.split("\\s*,\\s*")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // Para que Angular pueda leer el nombre del archivo al descargar el Excel
        config.setExposedHeaders(List.of("Content-Disposition"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
