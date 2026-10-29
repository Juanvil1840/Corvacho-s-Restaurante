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

// Una linea del pedido en el historial del cliente: cantidad, producto, adicionales y subtotal
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"cantidad", "producto", "adicionales", "subtotal"})
public class DetalleHistorialDTO {

    @JsonProperty("cantidad")
    private int cantidad;

    @JsonProperty("producto")
    private String producto;

    @JsonProperty("adicionales")
    private List<AdicionalHistorialDTO> adicionales;

    @JsonProperty("subtotal")
    private double subtotal;

    public static DetalleHistorialDTO fromEntity(DetallePedido detalle) {
        if (detalle == null) {
            return null;
        }

        List<AdicionalSeleccionado> seleccionados = new ArrayList<>();
        if (detalle.getAdicionales() != null) {
            seleccionados.addAll(detalle.getAdicionales());
        }

        int cantidad = detalle.getCantidad() > 0 ? detalle.getCantidad() : 1;
        double precioBase = detalle.getProducto() != null ? detalle.getProducto().getPrecio() : 0;
        double extras = seleccionados.stream()
                .mapToDouble(a -> a.getPrecio() * (a.getCantidad() > 0 ? a.getCantidad() : 1))
                .sum();

        return new DetalleHistorialDTO(
                cantidad,
                detalle.getProducto() != null ? detalle.getProducto().getNombre() : "",
                seleccionados.stream().map(AdicionalHistorialDTO::fromEntity).toList(),
                (precioBase + extras) * cantidad
        );
    }
}
