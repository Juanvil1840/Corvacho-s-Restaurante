package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Categoria;

public interface CategoriaService {
    Collection<Categoria> findAll();
    Categoria findById(Long id);
    Categoria guardar(Categoria categoria);
    void deleteById(Long id);
    boolean existsById(Long id);

   
    boolean tieneProductosOAdicionales(Long id);
}