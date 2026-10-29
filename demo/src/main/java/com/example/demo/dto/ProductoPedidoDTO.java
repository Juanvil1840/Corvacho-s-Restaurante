package com.example.demo.dto;

import com.example.demo.entities.Producto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Producto dentro del detalle de un pedido (sin categoria ni disponibilidad)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "nombre", "precio", "descripcion", "imagen"})
public class ProductoPedidoDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("precio")
    private double precio;

    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("imagen")
    private String imagen;

    // Convierte una entidad Producto a ProductoPedidoDTO
    public static ProductoPedidoDTO fromEntity(Producto producto) {
        if (producto == null) {
            return null;
        }
        return new ProductoPedidoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getDescripcion(),
                producto.getImagen()
        );
    }
}
