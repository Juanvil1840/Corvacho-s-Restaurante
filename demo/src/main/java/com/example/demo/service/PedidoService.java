package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Pedido;

public interface PedidoService {
    Collection<Pedido> findAll();
    Pedido findById(Long id);
    Pedido guardar(Pedido pedido);
    Pedido crearPedido(Pedido pedido);
    Pedido actualizarPedido(Long id, Pedido pedido);
    void deleteById(Long id);

    // Obtiene todos los pedidos de un cliente específico
    List<Pedido> findByClienteId(Long clienteId);

    // Obtiene los detalles de un pedido específico
    List<DetallePedido> findDetallesByPedidoId(Long pedidoId);
}