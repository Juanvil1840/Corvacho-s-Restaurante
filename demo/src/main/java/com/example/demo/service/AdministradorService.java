package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Administrador;

public interface AdministradorService {
    Collection<Administrador> findAll();
    Administrador findById(Long id);
    Administrador guardar(Administrador administrador);
    void deleteById(Long id);

}
