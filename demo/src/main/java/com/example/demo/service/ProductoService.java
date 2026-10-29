package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Producto;

// Interfaz de servicio para la gestion de productos del menu
public interface ProductoService {
    // Obtiene todos los productos registrados
    Collection<Producto> obtenerTodos();
    // Obtiene unicamente los productos disponibles
    Collection<Producto> obtenerDisponibles();
    // Busca un producto por su id
    Producto obtenerPorId(Long id);
    // Crea un nuevo producto validando su categoria
    Producto crearProducto(Producto producto);
    // Actualiza los campos de un producto
    Producto actualizar(Producto producto);
    // Actualiza un producto existente por su id
    Producto actualizarProducto(Long id, Producto producto);
    // Elimina un producto si no tiene pedidos asociados
    void eliminar(Long id);
    // Verifica si existe un producto con el id indicado
    boolean existe(Long id);
    // Comprueba si el producto aparece en algun pedido
    boolean tienePedidos(Long id);
}
