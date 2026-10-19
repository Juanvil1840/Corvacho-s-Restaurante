package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Producto;

public interface ProductoService {
    Collection<Producto> obtenerTodos();
    Producto obtenerPorId(Long id);
    Producto guardar(Producto producto);
    Producto actualizar(Producto producto);
    void eliminar(Long id);
    boolean existe(Long id);
    boolean tienePedidos(Long id);

    
    Collection<Producto> obtenerDisponibles();
}