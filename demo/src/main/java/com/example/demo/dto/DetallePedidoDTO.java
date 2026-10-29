package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.entities.AdicionalSeleccionado;
import com.example.demo.entities.DetallePedido;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO que representa un item dentro de un pedido con su producto, cantidad y adicionales
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id_DetallePedido", "cantidad", "producto", "adicionales"})
public class DetallePedidoDTO {

    @JsonProperty("id_DetallePedido")
    private Long id_DetallePedido;

    @JsonProperty("cantidad")
    private int cantidad;

    @JsonProperty("producto")
    private ProductoPedidoDTO producto;

    @JsonProperty("adicionales")
    private List<AdicionalPedidoDTO> adicionales = new ArrayList<>();

    // Convierte una entidad DetallePedido y su lista de adicionales a DetallePedidoDTO
    public static DetallePedidoDTO fromEntity(DetallePedido detallePedido) {
        if (detallePedido == null) {
            return null;
        }
        List<AdicionalPedidoDTO> adicionales = new ArrayList<>();
        if (detallePedido.getAdicionales() != null) {
            for (AdicionalSeleccionado adicional : detallePedido.getAdicionales()) {
                adicionales.add(AdicionalPedidoDTO.fromEntity(adicional));
            }
        }
        return new DetallePedidoDTO(
                detallePedido.getId_DetallePedido(),
                detallePedido.getCantidad(),
                ProductoPedidoDTO.fromEntity(detallePedido.getProducto()),
                adicionales
        );
    }
}
