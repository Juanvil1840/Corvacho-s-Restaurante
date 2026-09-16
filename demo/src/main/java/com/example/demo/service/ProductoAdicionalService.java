package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.ProductoAdicional;

public interface ProductoAdicionalService {
    Collection<ProductoAdicional> findAll();
    ProductoAdicional findById(Long id);
    ProductoAdicional guardar (ProductoAdicional productoAdicional);
    void deleteById(Long id);
}
