package com.example.demo.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Hace el login: valida usuario y contrasena con el AuthenticationManager de Spring,
// revisa que el usuario tenga el rol del login que uso y genera el token JWT
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;

    public AuthService(AuthenticationManager authenticationManager, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
    }

    public String login(String username, String contrasena, String rolEsperado) {
        if (username == null || username.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario y contraseña son obligatorios.");
        }
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username.trim(), contrasena));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        }
        // Un operador no puede entrar por el login de administrador (y al reves)
        boolean tieneRol = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(rolEsperado));
        if (!tieneRol) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        }
        return jwtGenerator.generateToken(authentication);
    }
}
