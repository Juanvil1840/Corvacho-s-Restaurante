package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.entities.AdicionalSeleccionado;
import com.example.demo.entities.DetallePedido;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_DetallePedido", "cantidad", "producto", "pedido", "adicional", "adicionales"})
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

    @JsonProperty("adicionales")
    private List<AdicionalSeleccionado> adicionales = new ArrayList<>();

    public DetallePedidoDTO(Long id_DetallePedido, int cantidad, ProductoDTO producto) {
        this(id_DetallePedido, cantidad, producto, null, null, new ArrayList<>());
    }

    public DetallePedidoDTO(Long id_DetallePedido, int cantidad, ProductoDTO producto, PedidoResumenDTO pedido, AdicionalDTO adicional) {
        this(id_DetallePedido, cantidad, producto, pedido, adicional, new ArrayList<>());
    }

    public DetallePedidoDTO(Long id_DetallePedido, int cantidad, ProductoDTO producto, PedidoResumenDTO pedido, AdicionalDTO adicional, List<AdicionalSeleccionado> adicionales) {
        this.id_DetallePedido = id_DetallePedido;
        this.cantidad = cantidad;
        this.producto = producto;
        this.pedido = pedido;
        this.adicional = adicional;
        this.adicionales = adicionales != null ? adicionales : new ArrayList<>();
    }

    public static DetallePedidoDTO fromEntity(DetallePedido detallePedido) {
        if (detallePedido == null) {
            return null;
        }
        List<AdicionalSeleccionado> listaAdic = new ArrayList<>();
        if (detallePedido.getAdicionales() != null && !detallePedido.getAdicionales().isEmpty()) {
            listaAdic.addAll(detallePedido.getAdicionales());
        } else if (detallePedido.getAdicional() != null) {
            listaAdic.add(new AdicionalSeleccionado(
                    detallePedido.getAdicional().getId_adicional(),
                    detallePedido.getAdicional().getNombre(),
                    detallePedido.getAdicional().getPrecio(),
                    1
            ));
        }

        return new DetallePedidoDTO(
                detallePedido.getId_DetallePedido(),
                detallePedido.getCantidad(),
                ProductoDTO.fromEntity(detallePedido.getProducto()),
                PedidoResumenDTO.fromEntity(detallePedido.getPedido()),
                AdicionalDTO.fromEntity(detallePedido.getAdicional()),
                listaAdic
        );
    }
}
