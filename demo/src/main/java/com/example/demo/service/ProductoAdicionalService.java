package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import com.example.demo.entities.ProductoAdicional;

public interface ProductoAdicionalService {
    Collection<ProductoAdicional> findAll();
    ProductoAdicional findById(Long id);
    ProductoAdicional guardar (ProductoAdicional productoAdicional);
    void deleteById(Long id);
    List<ProductoAdicional> buscarPorCategoria(Long categoriaId);
}
