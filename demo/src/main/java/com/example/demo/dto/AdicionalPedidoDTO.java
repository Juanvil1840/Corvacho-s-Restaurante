package com.example.demo.dto;

import com.example.demo.entities.AdicionalSeleccionado;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Adicional elegido dentro de un pedido o del carrito, con su cantidad
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id_adicional", "nombre", "precio", "cantidad"})
public class AdicionalPedidoDTO {

    @NotNull
    @JsonProperty("id_adicional")
    private Long id_adicional;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("precio")
    private double precio;

    @Min(1)
    @JsonProperty("cantidad")
    private int cantidad;

    public static AdicionalPedidoDTO fromEntity(AdicionalSeleccionado adicional) {
        if (adicional == null) {
            return null;
        }
        return new AdicionalPedidoDTO(
                adicional.getId_adicional(),
                adicional.getNombre(),
                adicional.getPrecio(),
                adicional.getCantidad()
        );
    }
}
