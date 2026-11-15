package com.example.demo.service;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Administrador;
import com.example.demo.entities.Role;
import com.example.demo.repository.AdministradorRepository;

// Implementacion del servicio para la gestion de administradores
@Service
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final UsuarioService usuarioService;

    // Constructor para inyectar el repositorio de administradores y el servicio de usuarios (seguridad)
    public AdministradorServiceImpl(AdministradorRepository administradorRepository, UsuarioService usuarioService) {
        this.administradorRepository = administradorRepository;
        this.usuarioService = usuarioService;
    }

    // Obtiene todos los administradores registrados
    @Override
    public Collection<Administrador> findAll() {
        return administradorRepository.findAll();
    }

    // Busca un administrador por su id o devuelve null si no existe
    @Override
    public Administrador findById(Long id) {
        return administradorRepository.findById(id).orElse(null);
    }

    // Guarda un administrador en la base de datos
    @Override
    public Administrador guardar(Administrador administrador) {
        return administradorRepository.save(administrador);
    }

    // Valida los datos y registra un nuevo administrador
    @Override
    public Administrador crearAdministrador(Administrador admin) {
        validarAdministrador(admin, null);
        admin.setId_administrador(null);
        // Se crea su usuario de seguridad con rol ADMIN y la contrasena se guarda encriptada
        admin.setUser(usuarioService.crearUsuario(admin.getUsuario(), admin.getContrasena(), Role.ADMIN));
        admin.setContrasena(usuarioService.encriptar(admin.getContrasena()));
        return administradorRepository.save(admin);
    }

    // Actualiza los datos de un administrador existente; la contrasena solo cambia si se envia una nueva
    @Override
    public Administrador actualizarAdministrador(Long id, Administrador admin) {
        Administrador existente = administradorRepository.findById(id).orElse(null);
        if (existente != null) {
            validarAdministrador(admin, id);
            existente.setUser(usuarioService.actualizarUsuario(existente.getUser(), admin.getUsuario(),
                    admin.getContrasena(), existente.getContrasena(), Role.ADMIN));
            existente.setNombre(admin.getNombre());
            existente.setUsuario(admin.getUsuario());
            // Solo actualiza la contrasena si no viene vacia (se guarda encriptada)
            if (admin.getContrasena() != null && !admin.getContrasena().isBlank()) {
                existente.setContrasena(usuarioService.encriptar(admin.getContrasena()));
            }
            return administradorRepository.save(existente);
        }
        return null;
    }

    // Elimina un administrador por su id
    @Override
    public void deleteById(Long id) {
        administradorRepository.deleteById(id);
    }

    // Verifica las credenciales de acceso de un administrador
    @Override
    public Administrador autenticar(String usuario, String contrasena) {
        Administrador admin = administradorRepository.findByUsuario(usuario);
        if (admin != null && usuarioService.coincide(contrasena, admin.getContrasena())) {
            return admin;
        }
        return null;
    }

    // Valida que los campos obligatorios esten presentes y que el nombre de usuario no este repetido
    private void validarAdministrador(Administrador admin, Long idActual) {
        if (admin.getNombre() == null || admin.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del administrador es obligatorio");
        }
        if (admin.getUsuario() == null || admin.getUsuario().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario del administrador es obligatorio");
        }
        if (idActual == null && (admin.getContrasena() == null || admin.getContrasena().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña del administrador es obligatoria");
        }
        Administrador conMismoUsuario = administradorRepository.findByUsuario(admin.getUsuario().trim());
        if (conMismoUsuario != null && !conMismoUsuario.getId_administrador().equals(idActual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está registrado");
        }
    }
}
