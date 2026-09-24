package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Producto;
import com.example.demo.errors.ProductoNotFoundException;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.ProductoAdicionalRepository;
import com.example.demo.repository.ProductoCarritoRepository;
import com.example.demo.repository.ProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private ProductoCarritoRepository productoCarritoRepository;

    @Autowired
    private ProductoAdicionalRepository productoAdicionalRepository;

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

        existente.setNombre(producto.getNombre());
        existente.setPrecio(producto.getPrecio());
        existente.setDescripcion(producto.getDescripcion());
        existente.setImagen(producto.getImagen());
        existente.setDisponible(producto.isDisponible());

        if (producto.getCategoria() != null) {
            existente.setCategoria(producto.getCategoria());
        }

        return productoRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            if (producto.getDetallePedidos() != null && !producto.getDetallePedidos().isEmpty()) {
                detallePedidoRepository.deleteAll(producto.getDetallePedidos());
            }
            if (producto.getProductoCarritos() != null && !producto.getProductoCarritos().isEmpty()) {
                productoCarritoRepository.deleteAll(producto.getProductoCarritos());
            }
            if (producto.getProductoAdicionales() != null && !producto.getProductoAdicionales().isEmpty()) {
                productoAdicionalRepository.deleteAll(producto.getProductoAdicionales());
            }
            productoRepository.delete(producto);
        }
    }

    @Override
    public boolean existe(Long id) {
        return productoRepository.existsById(id);
    }
}