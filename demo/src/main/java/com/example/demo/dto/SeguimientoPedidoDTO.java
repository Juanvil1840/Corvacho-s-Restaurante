package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Pedido;

// Lo que ve el cliente en "Tu pedido": estado actual, hora estimada de llegada y quien lo lleva.
// Es lo que se envia por el canal en vivo (Server-Sent Events) cada vez que el pedido cambia.
public record SeguimientoPedidoDTO(
        Long pedidoId,
        String estado,
        // 1 = Recibido, 2 = Cocinando, 3 = Enviado, 4 = Entregado
        int paso,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaEntrega,
        // Hora aproximada de llegada segun el tiempo promedio real de entrega (null si ya se entrego)
        LocalDateTime llegadaEstimada,
        long minutosPromedioEntrega,
        String domiciliario,
        String celularDomiciliario) {

    private static final String[] ESTADOS = { "Recibido", "Cocinando", "Enviado", "Entregado" };

    public static SeguimientoPedidoDTO fromEntity(Pedido pedido, long minutosPromedio) {
        int paso = 1;
        for (int i = 0; i < ESTADOS.length; i++) {
            if (ESTADOS[i].equalsIgnoreCase(pedido.getEstado())) {
                paso = i + 1;
            }
        }
        boolean entregado = paso == ESTADOS.length;
        LocalDateTime llegada = !entregado && pedido.getFechaCreacion() != null
                ? pedido.getFechaCreacion().plusMinutes(minutosPromedio)
                : null;

        // Los datos del domiciliario solo se comparten cuando el pedido ya salio del restaurante
        Domiciliario domiciliario = paso >= 3 ? pedido.getDomiciliario() : null;

        return new SeguimientoPedidoDTO(
                pedido.getId_Pedido(),
                ESTADOS[paso - 1],
                paso,
                pedido.getFechaCreacion(),
                pedido.getFechaEntrega(),
                llegada,
                minutosPromedio,
                domiciliario != null ? domiciliario.getNombre() : null,
                domiciliario != null ? domiciliario.getCelular() : null);
    }
}
