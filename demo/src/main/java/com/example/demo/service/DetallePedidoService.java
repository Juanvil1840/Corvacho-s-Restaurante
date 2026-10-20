package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import com.example.demo.entities.DetallePedido;

public interface DetallePedidoService {
    Collection<DetallePedido> findAll();
    DetallePedido findById(Long id);
    DetallePedido guardar(DetallePedido detallepedido);
    void deleteById(Long id);
    List<DetallePedido> findByPedidoId(Long pedidoId);
}
