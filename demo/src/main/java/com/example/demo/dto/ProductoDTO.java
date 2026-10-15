package com.example.demo.dto;

import com.example.demo.entities.Producto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id", "nombre", "precio", "descripcion", "imagen", "disponible", "categoria"})
public class ProductoDTO {

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

    @JsonProperty("disponible")
    private boolean disponible;

    @JsonProperty("categoria")
    private CategoriaDTO categoria;

    public ProductoDTO(Long id, String nombre, double precio, String descripcion, String imagen, boolean disponible, CategoriaDTO categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.disponible = disponible;
        this.categoria = categoria;
    }

    public static ProductoDTO fromEntity(Producto producto) {
        if (producto == null) {
            return null;
        }
        return new ProductoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getDescripcion(),
                producto.getImagen(),
                producto.isDisponible(),
                CategoriaDTO.fromEntity(producto.getCategoria())
        );
    }
}
