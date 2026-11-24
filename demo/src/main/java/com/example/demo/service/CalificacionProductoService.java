package com.example.demo.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.CrearCalificacionProductoDTO;
import com.example.demo.dto.ProductoCalificacionResumenDTO;
import com.example.demo.entities.CalificacionProducto;
import com.example.demo.repository.CalificacionProductoRepository;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;

@Service
public class CalificacionProductoService {
    private final CalificacionProductoRepository calificacionRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public CalificacionProductoService(CalificacionProductoRepository calificacionRepository,
            PedidoRepository pedidoRepository, ProductoRepository productoRepository,
            DetallePedidoRepository detallePedidoRepository) {
        this.calificacionRepository = calificacionRepository;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional
    public CalificacionProducto crear(Long pedidoId, Long productoId, CrearCalificacionProductoDTO solicitud) {
        if (calificacionRepository.existsByPedidoAndProducto(pedidoId, productoId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este producto ya fue calificado en este pedido");
        }
        var pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el pedido"));
        if (!"Entregado".equalsIgnoreCase(pedido.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo puedes calificar productos de pedidos entregados");
        }
        if (!detallePedidoRepository.existsProductoEnPedido(pedidoId, productoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El producto no pertenece a este pedido");
        }
        var producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el producto"));
        var calificacion = new CalificacionProducto();
        calificacion.setPedido(pedido);
        calificacion.setProducto(producto);
        calificacion.setPuntuacion(solicitud.puntuacion());
        try {
            return calificacionRepository.saveAndFlush(calificacion);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este producto ya fue calificado en este pedido");
        }
    }

    @Transactional(readOnly = true)
    public List<CalificacionProducto> porCliente(Long clienteId) {
        return calificacionRepository.findByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public Map<Long, ProductoCalificacionResumenDTO> resumenes(Collection<Long> productoIds) {
        if (productoIds == null || productoIds.isEmpty()) return Map.of();
        return calificacionRepository.resumirPorProductos(productoIds).stream()
                .collect(Collectors.toMap(ProductoCalificacionResumenDTO::productoId, Function.identity()));
    }
}
