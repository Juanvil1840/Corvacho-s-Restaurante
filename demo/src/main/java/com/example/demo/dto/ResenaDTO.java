package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.entities.Resena;

public record ResenaDTO(
        Long id,
        Long pedidoId,
        String estadoPedido,
        Long clienteId,
        String cliente,
        String correoCliente,
        Integer puntuacion,
        String comentario,
        LocalDateTime fechaCreacion) {

    public static ResenaDTO fromEntity(Resena resena) {
        var pedido = resena.getPedido();
        var cliente = pedido.getCliente();
        return new ResenaDTO(resena.getId(), pedido.getId_Pedido(), pedido.getEstado(),
                cliente.getClienteId(), cliente.getNombre() + " " + cliente.getApellido(), cliente.getCorreo(),
                resena.getPuntuacion(), resena.getComentario(), resena.getFechaCreacion());
    }
}
