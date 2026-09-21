package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import com.example.demo.entities.Pedido;

public interface PedidoService {
    Collection<Pedido> findAll();
    Pedido findById(Long id);
    Pedido guardar(Pedido pedido);
    void deleteById(Long id);
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findAllOrdenadosPorFecha();
}
