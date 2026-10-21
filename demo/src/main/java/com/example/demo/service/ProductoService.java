package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Producto;

public interface ProductoService {
    Collection<Producto> obtenerTodos();
    Collection<Producto> obtenerDisponibles();
    Producto obtenerPorId(Long id);
    Producto guardar(Producto producto);
    Producto crearProducto(Producto producto);
    Producto actualizar(Producto producto);
    Producto actualizarProducto(Long id, Producto producto);
    void eliminar(Long id);
    boolean existe(Long id);
    boolean tienePedidos(Long id);
}
