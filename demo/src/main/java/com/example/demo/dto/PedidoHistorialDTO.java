package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Pedido;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Pedido en el historial del cliente. No trae cliente, operador ni domiciliario
// porque el cliente ya sabe quien es.
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id_Pedido", "estado", "fechaCreacion", "total", "productos"})
public class PedidoHistorialDTO {

    @JsonProperty("id_Pedido")
    private Long id_Pedido;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;

    @JsonProperty("total")
    private double total;

    @JsonProperty("productos")
    private List<DetalleHistorialDTO> productos;

    public static PedidoHistorialDTO fromEntity(Pedido pedido, List<DetallePedido> detalles) {
        if (pedido == null) {
            return null;
        }

        List<DetalleHistorialDTO> productos = detalles != null
                ? detalles.stream().map(DetalleHistorialDTO::fromEntity).toList()
                : Collections.emptyList();

        double total = productos.stream().mapToDouble(DetalleHistorialDTO::getSubtotal).sum();

        return new PedidoHistorialDTO(
                pedido.getId_Pedido(),
                pedido.getEstado(),
                pedido.getFechaCreacion(),
                total,
                productos
        );
    }
}
