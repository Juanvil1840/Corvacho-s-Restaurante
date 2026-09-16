package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Adicional;

public interface AdicionalService {
    Collection<Adicional> findAll();
    Adicional findById(Long id);
    Adicional guardar(Adicional adicional);
    void deleteById(Long id);
}
