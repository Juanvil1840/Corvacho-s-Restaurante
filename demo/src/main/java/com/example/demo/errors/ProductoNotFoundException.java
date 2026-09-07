package com.example.demo.errors;

// Excepcion cuando no se encuentra un producto, con su id en formato Long
public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(Long id) {
        super("Producto no encontrado con el ID: " + id);
    }
}