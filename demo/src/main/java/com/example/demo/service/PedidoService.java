package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Pedido;

// Interfaz de servicio para la gestion completa de pedidos
public interface PedidoService {
    // Lista todos los pedidos registrados
    Collection<Pedido> findAll();
    // Busca un pedido por su id
    Pedido findById(Long id);
    // Guarda directamente un pedido
    Pedido guardar(Pedido pedido);
    // Crea un nuevo pedido validando cliente, domiciliario, estado y detalles
    Pedido crearPedido(Pedido pedido);
    // Actualiza un pedido existente controlando las transiciones de estado y disponibilidad del domiciliario
    Pedido actualizarPedido(Long id, Pedido pedido);
    // Elimina un pedido por su id
    void deleteById(Long id);

    // Obtiene todos los pedidos activos (no entregados)
    Collection<Pedido> findActivos();

    // Obtiene todos los pedidos de un cliente específico
    List<Pedido> findByClienteId(Long clienteId);

    // Obtiene los detalles de un pedido específico
    List<DetallePedido> findDetallesByPedidoId(Long pedidoId);
}
