package com.example.demo.dto;

import com.example.demo.entities.Categoria;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO que representa una categoria de productos y adicionales
@Data
@NoArgsConstructor
@JsonPropertyOrder({"id", "nombre"})
public class CategoriaDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nombre")
    private String nombre;

    // Constructor con id y nombre de la categoria
    public CategoriaDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Convierte una entidad Categoria a CategoriaDTO
    public static CategoriaDTO fromEntity(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return new CategoriaDTO(
                categoria.getId(),
                categoria.getNombre()
        );
    }
}
