package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Administrador;
import com.example.demo.repository.AdministradorRepository;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

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
    public Administrador crearAdministrador(Administrador administrador) {
        if (administrador.getNombre() == null || administrador.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del administrador es obligatorio");
        }
        administrador.setId_administrador(null);
        return administradorRepository.save(administrador);
    }

    @Override
    public Administrador actualizarAdministrador(Long id, Administrador administrador) {
        Administrador existente = findById(id);
        if (existente == null) {
            return null;
        }
        if (administrador.getNombre() == null || administrador.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del administrador es obligatorio");
        }
        existente.setNombre(administrador.getNombre());
        return administradorRepository.save(existente);
    }

    @Override
    public void deleteById(Long id) {
        administradorRepository.deleteById(id);
    }
}
