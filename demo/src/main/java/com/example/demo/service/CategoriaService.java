package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Categoria;

// Interfaz de servicio para la gestion de categorias
public interface CategoriaService {
    // Lista todas las categorias
    Collection<Categoria> findAll();
    // Busca una categoria por su id
    Categoria findById(Long id);
    // Guarda o actualiza una categoria
    Categoria guardar(Categoria categoria);
    // Elimina una categoria por su id
    void deleteById(Long id);
    // Verifica si existe una categoria con el id indicado
    boolean existsById(Long id);

    // Comprueba si la categoria tiene productos o adicionales asociados
    boolean tieneProductosOAdicionales(Long id);
}