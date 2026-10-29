package com.example.demo.service;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Administrador;
import com.example.demo.repository.AdministradorRepository;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;

    public AdministradorServiceImpl(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @Override
    public Collection<Administrador> findAll() {
        return administradorRepository.findAll();
    }

    @Override
    public Administrador findById(Long id) {
        return administradorRepository.findById(id).orElse(null);
    }
    @Override
    public Administrador guardar(Administrador administrador) {
        return administradorRepository.save(administrador);
    }
    @Override
    public Administrador crearAdministrador(Administrador admin) {
        validarAdministrador(admin, null);
        admin.setId_administrador(null);
        return administradorRepository.save(admin);
    }

    @Override
    public Administrador actualizarAdministrador(Long id, Administrador admin) {
        Administrador existente = administradorRepository.findById(id).orElse(null);
        if (existente != null) {
            validarAdministrador(admin, id);
            existente.setNombre(admin.getNombre());
            existente.setUsuario(admin.getUsuario());
            if (admin.getContrasena() != null && !admin.getContrasena().isBlank()) {
                existente.setContrasena(admin.getContrasena());
            }
            return administradorRepository.save(existente);
        }
        return null;
    }

    @Override
    public void deleteById(Long id) {
        administradorRepository.deleteById(id);
    }

    @Override
    public Administrador autenticar(String usuario, String contrasena) {
        Administrador admin = administradorRepository.findByUsuario(usuario);
        if (admin != null && admin.getContrasena().equals(contrasena)) {
            return admin;
        }
        return null;
    }

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