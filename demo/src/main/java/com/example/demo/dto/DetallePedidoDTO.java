package com.example.demo.dto;

import com.example.demo.entities.DetallePedido;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_DetallePedido", "cantidad", "producto", "pedido", "adicional"})
public class DetallePedidoDTO {

    @JsonProperty("id_DetallePedido")
    private Long id_DetallePedido;

    @JsonProperty("cantidad")
    private int cantidad;

    @JsonProperty("producto")
    private ProductoDTO producto;

    @JsonProperty("pedido")
    private PedidoResumenDTO pedido;

    @JsonProperty("adicional")
    private AdicionalDTO adicional;

    public DetallePedidoDTO(Long id_DetallePedido, int cantidad, ProductoDTO producto) {
        this(id_DetallePedido, cantidad, producto, null, null);
    }

    public DetallePedidoDTO(Long id_DetallePedido, int cantidad, ProductoDTO producto, PedidoResumenDTO pedido, AdicionalDTO adicional) {
        this.id_DetallePedido = id_DetallePedido;
        this.cantidad = cantidad;
        this.producto = producto;
        this.pedido = pedido;
        this.adicional = adicional;
    }

    public static DetallePedidoDTO fromEntity(DetallePedido detallePedido) {
        if (detallePedido == null) {
            return null;
        }
        return new DetallePedidoDTO(
                detallePedido.getId_DetallePedido(),
                detallePedido.getCantidad(),
                ProductoDTO.fromEntity(detallePedido.getProducto()),
                PedidoResumenDTO.fromEntity(detallePedido.getPedido()),
                AdicionalDTO.fromEntity(detallePedido.getAdicional())
        );
    }
}
