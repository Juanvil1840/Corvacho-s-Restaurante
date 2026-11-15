package com.example.demo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Administrador;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Operador;
import com.example.demo.entities.Role;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.service.UsuarioService;

// Se ejecuta despues del DataLoader: crea los roles y le asigna un UserEntity (con la contrasena
// encriptada) a cada administrador, operador y cliente que todavia no tenga uno.
// Asi tambien funcionan las bases H2 que ya existian antes de agregar la seguridad.
@Component
@Order(2)
public class UsuariosDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuariosDataLoader.class);

    private final UsuarioService usuarioService;
    private final AdministradorRepository administradorRepository;
    private final OperadorRepository operadorRepository;
    private final ClienteRepository clienteRepository;

    public UsuariosDataLoader(UsuarioService usuarioService, AdministradorRepository administradorRepository,
            OperadorRepository operadorRepository, ClienteRepository clienteRepository) {
        this.usuarioService = usuarioService;
        this.administradorRepository = administradorRepository;
        this.operadorRepository = operadorRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        usuarioService.obtenerRol(Role.ADMIN);
        usuarioService.obtenerRol(Role.OPERADOR);
        usuarioService.obtenerRol(Role.CLIENTE);

        for (Administrador admin : administradorRepository.findAll()) {
            if (admin.getUser() == null) {
                try {
                    admin.setUser(usuarioService.crearUsuario(admin.getUsuario(), admin.getContrasena(), Role.ADMIN));
                    admin.setContrasena(usuarioService.encriptar(admin.getContrasena()));
                    administradorRepository.save(admin);
                } catch (ResponseStatusException e) {
                    log.warn("No se pudo crear el usuario del administrador {}: {}", admin.getUsuario(), e.getReason());
                }
            }
        }
        for (Operador operador : operadorRepository.findAll()) {
            if (operador.getUser() == null) {
                try {
                    operador.setUser(usuarioService.crearUsuario(operador.getUsuario(), operador.getContrasena(), Role.OPERADOR));
                    operador.setContrasena(usuarioService.encriptar(operador.getContrasena()));
                    operadorRepository.save(operador);
                } catch (ResponseStatusException e) {
                    log.warn("No se pudo crear el usuario del operador {}: {}", operador.getUsuario(), e.getReason());
                }
            }
        }
        for (Cliente cliente : clienteRepository.findAll()) {
            if (cliente.getUser() == null) {
                try {
                    cliente.setUser(usuarioService.crearUsuario(cliente.getCorreo(), cliente.getContrasena(), Role.CLIENTE));
                    cliente.setContrasena(usuarioService.encriptar(cliente.getContrasena()));
                    clienteRepository.save(cliente);
                } catch (ResponseStatusException e) {
                    log.warn("No se pudo crear el usuario del cliente {}: {}", cliente.getCorreo(), e.getReason());
                }
            }
        }
    }
}
