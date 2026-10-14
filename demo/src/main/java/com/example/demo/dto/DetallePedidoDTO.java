package com.example.demo.dto;

import com.example.demo.entities.DetallePedido;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_DetallePedido", "cantidad", "producto"})
public class DetallePedidoDTO {

    @JsonProperty("id_DetallePedido")
    private Long id_DetallePedido;

    @JsonProperty("cantidad")
    private int cantidad;

    @JsonProperty("producto")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ProductoPedidoDTO producto;

    public DetallePedidoDTO(Long id_DetallePedido, int cantidad, ProductoPedidoDTO producto) {
        this.id_DetallePedido = id_DetallePedido;
        this.cantidad = cantidad;
        this.producto = producto;
    }

    public static DetallePedidoDTO fromEntity(DetallePedido detallePedido) {
        if (detallePedido == null) {
            return null;
        }
        return new DetallePedidoDTO(
                detallePedido.getId_DetallePedido(),
                detallePedido.getCantidad(),
                ProductoPedidoDTO.fromEntity(detallePedido.getProducto())
        );
    }
}
