package com.example.demo.service;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Categoria;
import com.example.demo.entities.Producto;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.ProductoCarritoRepository;
import com.example.demo.repository.ProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoCarritoRepository productoCarritoRepository;

    public ProductoServiceImpl(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            DetallePedidoRepository detallePedidoRepository,
            ProductoCarritoRepository productoCarritoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoCarritoRepository = productoCarritoRepository;
    }

    @Override
    public Collection<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Collection<Producto> obtenerDisponibles() {
        return productoRepository.findByDisponibleTrue();
    }

    @Override
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElse(null);
    }

    @Override
    public Producto crearProducto(Producto producto) {
        producto.setId(null);
        producto.setCategoria(buscarCategoria(producto));
        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizarProducto(Long id, Producto producto) {
        if (!existe(id)) {
            return null;
        }
        producto.setId(id);
        producto.setCategoria(buscarCategoria(producto));
        return actualizar(producto);
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
            if (tienePedidos(id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Excepción: No se puede eliminar el producto porque está presente en un pedido.");
            }
            if (producto.getProductoCarritos() != null && !producto.getProductoCarritos().isEmpty()) {
                productoCarritoRepository.deleteAll(producto.getProductoCarritos());
            }
            productoRepository.delete(producto);
        }
    }

    @Override
    public boolean existe(Long id) {
        return productoRepository.existsById(id);
    }

    @Override
    public boolean tienePedidos(Long id) {
        return detallePedidoRepository.existsByProducto_Id(id);
    }

    private Categoria buscarCategoria(Producto producto) {
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar una categoría");
        }
        return categoriaRepository.findById(producto.getCategoria().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La categoría seleccionada no existe"));
    }
}
