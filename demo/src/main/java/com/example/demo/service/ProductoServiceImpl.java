package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Producto;
import com.example.demo.errors.ProductoNotFoundException;
import com.example.demo.repository.ProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Collection<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
    }

    @Override
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizar(Producto producto) {
        if (producto == null || producto.getId() == null) {
            return productoRepository.save(producto);
        }

        Producto existente = productoRepository.findById(producto.getId()).orElse(null);
        if (existente == null) {
            return productoRepository.save(producto);
        }

        if (producto.getCategoria() == null) {
            producto.setCategoria(existente.getCategoria());
        }
        if (producto.getProductoCarritos() == null) {
            producto.setProductoCarritos(existente.getProductoCarritos());
        }
        if (producto.getProductoAdicionales() == null) {
            producto.setProductoAdicionales(existente.getProductoAdicionales());
        }
        if (producto.getDetallePedidos() == null) {
            producto.setDetallePedidos(existente.getDetallePedidos());
        }

        return productoRepository.save(producto);
    }

    @Override
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public boolean existe(Long id) {
        return productoRepository.existsById(id);
    }
}