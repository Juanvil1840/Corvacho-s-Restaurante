package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.entities.CalificacionProducto;

public record CalificacionProductoDTO(
        Long id,
        Long pedidoId,
        Long productoId,
        String producto,
        Integer puntuacion,
        LocalDateTime fechaCreacion) {

    public static CalificacionProductoDTO fromEntity(CalificacionProducto calificacion) {
        return new CalificacionProductoDTO(calificacion.getId(),
                calificacion.getPedido().getId_Pedido(), calificacion.getProducto().getId(),
                calificacion.getProducto().getNombre(), calificacion.getPuntuacion(), calificacion.getFechaCreacion());
    }
}
