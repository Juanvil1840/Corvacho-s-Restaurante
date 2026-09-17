package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Domiciliario;

public interface DomiciliarioService {
    Collection<Domiciliario> findAll();
    Domiciliario findById(Long id);
    Domiciliario guardar(Domiciliario domiciliario);
    void deleteById(Long id);
    
}
