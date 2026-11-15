package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Role;
import com.example.demo.entities.UserEntity;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

// Crea y actualiza los UserEntity (usuario + contrasena encriptada + rol)
// de clientes, operadores y administradores
@Service
public class UsuarioService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Busca el rol por nombre y si no existe lo crea
    public Role obtenerRol(String nombre) {
        return roleRepository.findByName(nombre)
                .orElseGet(() -> roleRepository.save(new Role(nombre)));
    }

    // Encripta una contrasena; si ya viene encriptada con BCrypt la deja igual
    public String encriptar(String contrasena) {
        if (contrasena == null) {
            return null;
        }
        if (esBCrypt(contrasena)) {
            return contrasena;
        }
        return passwordEncoder.encode(contrasena);
    }

    // Compara una contrasena escrita por el usuario con la guardada (encriptada)
    public boolean coincide(String contrasenaPlana, String contrasenaGuardada) {
        if (contrasenaPlana == null || contrasenaGuardada == null) {
            return false;
        }
        if (esBCrypt(contrasenaGuardada)) {
            return passwordEncoder.matches(contrasenaPlana, contrasenaGuardada);
        }
        return contrasenaPlana.equals(contrasenaGuardada);
    }

    // Crea un UserEntity nuevo (sin guardarlo: se guarda en cascada con el cliente/operador/admin)
    public UserEntity crearUsuario(String username, String contrasena, String rol) {
        validarUsernameLibre(username, null);
        List<Role> roles = new ArrayList<>();
        roles.add(obtenerRol(rol));
        return new UserEntity(username.trim(), encriptar(contrasena), roles);
    }

    // Actualiza el usuario y, si llega una contrasena nueva, la encripta.
    // Si la persona todavia no tenia UserEntity se le crea uno con su contrasena actual
    public UserEntity actualizarUsuario(UserEntity user, String username, String contrasenaNueva,
            String contrasenaActual, String rol) {
        boolean hayNueva = contrasenaNueva != null && !contrasenaNueva.isBlank();
        if (user == null) {
            return crearUsuario(username, hayNueva ? contrasenaNueva : contrasenaActual, rol);
        }
        validarUsernameLibre(username, user.getId());
        user.setUsername(username.trim());
        if (hayNueva) {
            user.setPassword(encriptar(contrasenaNueva));
        }
        return user;
    }

    // El usuario (o correo) debe ser unico entre todos los tipos de usuario
    private void validarUsernameLibre(String username, Long idActual) {
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario es obligatorio");
        }
        userRepository.findByUsername(username.trim()).ifPresent(existente -> {
            if (!existente.getId().equals(idActual)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está registrado");
            }
        });
    }

    private boolean esBCrypt(String valor) {
        return valor.startsWith("$2a$") || valor.startsWith("$2b$") || valor.startsWith("$2y$");
    }
}
