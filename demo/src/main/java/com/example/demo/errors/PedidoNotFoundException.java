package com.example.demo.errors;

public class PedidoNotFoundException extends RuntimeException {
    public PedidoNotFoundException(Long id) {
        super("Pedido no encontrado con el ID: " + id);
    }
}