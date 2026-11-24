package com.example.demo.service;

// Evento interno de Spring que se publica cuando un pedido cambia (estado o domiciliario).
// Quien lo escucha (el seguimiento en vivo) no necesita que PedidoService lo conozca.
public record PedidoActualizadoEvent(Long pedidoId) {
}
