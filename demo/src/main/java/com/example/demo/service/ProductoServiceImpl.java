package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Producto;
import com.example.demo.repository.ProductoRepository;

@Service //Servicio
public class ProductoServiceImpl implements ProductoService {

    @Autowired //Inyeccion de dependencias
    private ProductoRepository productoRepository; //Variable productoRepository de ProductoRepository para utilizarla en service

    
    @Override
    public Collection<Producto> obtenerTodos() { //Obtener todos los productos
        return productoRepository.findAll(); //Con productosRepository utilizamos el finAll para encontrarlos a todos
    }

    @Override
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    // === NUEVOS MÉTODOS PARA CRUD ===
    @Override
    public Producto guardar(Producto producto) { //Guardar producto
        return productoRepository.save(producto); //Podemos guardar el nuevo producto o actualizar uno ya existente
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