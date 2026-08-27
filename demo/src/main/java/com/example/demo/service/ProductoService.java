package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Producto;

public interface ProductoService {
    Collection<Producto> obtenerTodos();
    Producto obtenerPorId(Integer id);
}