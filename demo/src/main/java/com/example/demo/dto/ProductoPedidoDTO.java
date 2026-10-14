package com.example.demo.dto;

import com.example.demo.entities.Producto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id", "nombre", "precio"})
public class ProductoPedidoDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("precio")
    private double precio;

    public ProductoPedidoDTO(Long id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public static ProductoPedidoDTO fromEntity(Producto producto) {
        if (producto == null) {
            return null;
        }
        return new ProductoPedidoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio()
        );
    }
}
