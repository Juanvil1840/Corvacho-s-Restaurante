package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.ProductoCarrito;

public interface ProductoCarritoService {
    Collection<ProductoCarrito> findAll();
    ProductoCarrito findById(Long id);
    ProductoCarrito guardar(ProductoCarrito productoCarrito);
    void deleteById(Long id);
    
}
