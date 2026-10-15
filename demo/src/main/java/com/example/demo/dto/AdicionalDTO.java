package com.example.demo.dto;

import com.example.demo.entities.Adicional;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_adicional", "nombre", "precio", "disponible", "categoria"})
public class AdicionalDTO {

    @JsonProperty("id_adicional")
    private Long id_adicional;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("precio")
    private double precio;

    @JsonProperty("disponible")
    private boolean disponible;

    @JsonProperty("categoria")
    private CategoriaDTO categoria;

    public AdicionalDTO(Long id_adicional, String nombre, double precio, boolean disponible, CategoriaDTO categoria) {
        this.id_adicional = id_adicional;
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
        this.categoria = categoria;
    }

    public static AdicionalDTO fromEntity(Adicional adicional) {
        if (adicional == null) {
            return null;
        }
        return new AdicionalDTO(
                adicional.getId_adicional(),
                adicional.getNombre(),
                adicional.getPrecio(),
                adicional.isDisponible(),
                CategoriaDTO.fromEntity(adicional.getCategoria())
        );
    }
}
