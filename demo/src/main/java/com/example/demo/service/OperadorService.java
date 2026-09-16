package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Operador;

public interface OperadorService {
    Collection<Operador> findAll();
    Operador findById(Long id);
    Operador guardar(Operador operador);
    void deleteById(Long id);
}
