package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import com.example.demo.entities.DetallePedido;

// Interfaz de servicio para la consulta y gestion de detalles de pedido
public interface DetallePedidoService {
    // Lista todos los detalles de pedido
    Collection<DetallePedido> findAll();
    // Busca un detalle de pedido por su id
    DetallePedido findById(Long id);
    // Guarda un detalle de pedido
    DetallePedido guardar(DetallePedido detallepedido);
    // Elimina un detalle de pedido por su id
    void deleteById(Long id);
    // Obtiene todos los detalles asociados a un pedido especifico
    List<DetallePedido> findByPedidoId(Long pedidoId);
}
