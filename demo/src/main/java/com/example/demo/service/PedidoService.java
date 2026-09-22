package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Pedido;

public interface PedidoService {
    Collection<Pedido> findAll();
    Pedido findById(Long id);
    Pedido guardar(Pedido pedido);
    void deleteById(Long id);
}
