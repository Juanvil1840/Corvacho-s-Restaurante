package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.ProductoCarrito;
import com.example.demo.repository.ProductoCarritoRepository;

@Service
public class ProductoCarritoServiceImpl implements ProductoCarritoService {

    @Autowired 
    private ProductoCarritoRepository productoCarritoRepository;

    @Override
    public Collection<ProductoCarrito> findAll() {
        return productoCarritoRepository.findAll();
    }

    @Override
    public ProductoCarrito findById(Long id) {
        return productoCarritoRepository.findById(id).orElse(null);
    }

    @Override
    public ProductoCarrito guardar(ProductoCarrito productoCarrito) {
        return productoCarritoRepository.save(productoCarrito);
    }

    @Override
    public void deleteById(Long id) {
        productoCarritoRepository.deleteById(id);
    }
    
}
