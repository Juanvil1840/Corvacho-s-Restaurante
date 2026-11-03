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

// Implementacion del servicio para la gestion de productos
@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoCarritoRepository productoCarritoRepository;

    // Constructor con inyeccion de repositorios
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

    // Lista todos los productos del restaurante
    @Override
    public Collection<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // Lista unicamente los productos marcados como disponibles para el menu
    @Override
    public Collection<Producto> obtenerDisponibles() {
        return productoRepository.findByDisponibleTrue();
    }

    // Busca un producto por su id o retorna null si no existe
    @Override
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElse(null);
    }

    // Crea un nuevo producto validando que tenga una categoria existente
    @Override
    public Producto crearProducto(Producto producto) {
        producto.setId(null);
        producto.setCategoria(buscarCategoria(producto));
        return productoRepository.save(producto);
    }

    // Actualiza un producto existente por su id validando su categoria
    @Override
    public Producto actualizarProducto(Long id, Producto producto) {
        if (!existe(id)) {
            return null;
        }
        producto.setId(id);
        producto.setCategoria(buscarCategoria(producto));
        return actualizar(producto);
    }

    // Actualiza los datos basicos y la categoria de una entidad Producto
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

    // Elimina un producto si no esta en ningun pedido; si esta en carritos, lo retira primero de los carritos
    @Override
    @Transactional
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            // Impide eliminar el producto si ya forma parte del historial de algun pedido
            if (tienePedidos(id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Excepción: No se puede eliminar el producto porque está presente en un pedido.");
            }
            // Elimina las referencias del producto en los carritos activos antes de borrarlo
            if (producto.getProductoCarritos() != null && !producto.getProductoCarritos().isEmpty()) {
                productoCarritoRepository.deleteAll(producto.getProductoCarritos());
            }
            productoRepository.delete(producto);
        }
    }

    // Comprueba si existe un producto con el id dado
    @Override
    public boolean existe(Long id) {
        return productoRepository.existsById(id);
    }

    // Verifica si el producto esta asociado a algun detalle de pedido
    @Override
    public boolean tienePedidos(Long id) {
        return detallePedidoRepository.existsByProducto_Id(id);
    }

    // Valida y obtiene la categoria seleccionada para el producto
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
