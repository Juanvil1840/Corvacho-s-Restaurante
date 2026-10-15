package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.entities.Pedido;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_Pedido", "estado", "fechaCreacion", "fechaEntrega"})
public class PedidoResumenDTO {

    @JsonProperty("id_Pedido")
    private Long id_Pedido;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;

    @JsonProperty("fechaEntrega")
    private LocalDateTime fechaEntrega;

    public PedidoResumenDTO(Long id_Pedido, String estado, LocalDateTime fechaCreacion, LocalDateTime fechaEntrega) {
        this.id_Pedido = id_Pedido;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaEntrega = fechaEntrega;
    }

    public static PedidoResumenDTO fromEntity(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        return new PedidoResumenDTO(
                pedido.getId_Pedido(),
                pedido.getEstado(),
                pedido.getFechaCreacion(),
                pedido.getFechaEntrega()
        );
    }
}
