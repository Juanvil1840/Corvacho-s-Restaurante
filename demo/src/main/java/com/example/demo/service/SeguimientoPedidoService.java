package com.example.demo.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.dto.SeguimientoPedidoDTO;

// Seguimiento en vivo de un pedido para el cliente (Server-Sent Events)
public interface SeguimientoPedidoService {

    // Estado actual del pedido con su hora estimada de llegada
    SeguimientoPedidoDTO consultar(Long pedidoId);

    // Abre un canal en vivo: envia el estado actual y despues cada cambio del pedido
    SseEmitter suscribir(Long pedidoId);
}
