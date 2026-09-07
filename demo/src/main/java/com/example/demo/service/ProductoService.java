package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Producto;

public interface ProductoService { // Interfaz, los metodos que debe tener el servicio de producto
    
    Collection<Producto> obtenerTodos(); //Obtener todos los productos
    Producto obtenerPorId(Long id); //Obtener solo uno en especifico mediante su id

    
    Producto guardar(Producto producto); //Guardar o actualizar un producto
    void eliminar(Long id); //Eliminar un producto
    boolean existe(Long id); //Verificar si el producto existe
}