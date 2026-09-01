package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Producto;

public interface ProductoService {
    // === MÉTODOS DE LECTURA (YA EXISTENTES) ===
    Collection<Producto> obtenerTodos();
    Producto obtenerPorId(Integer id);

    // === NUEVOS MÉTODOS PARA CRUD ===
    Producto guardar(Producto producto);
    void eliminar(Integer id);
    boolean existe(Integer id);
}