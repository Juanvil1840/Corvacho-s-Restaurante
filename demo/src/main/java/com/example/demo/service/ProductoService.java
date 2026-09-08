package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Producto;

public interface ProductoService {
    // === MÉTODOS DE LECTURA (YA EXISTENTES) ===
    Collection<Producto> obtenerTodos();
    Producto obtenerPorId(Long id);

    // === NUEVOS MÉTODOS PARA CRUD ===
    Producto guardar(Producto producto);
    void eliminar(Long id);
    boolean existe(Long id);
}