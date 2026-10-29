package com.example.demo.dto;

import com.example.demo.entities.AdicionalSeleccionado;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Adicional que se muestra en el historial del cliente
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"nombre", "cantidad"})
public class AdicionalHistorialDTO {

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("cantidad")
    private int cantidad;

    public static AdicionalHistorialDTO fromEntity(AdicionalSeleccionado adicional) {
        if (adicional == null) {
            return null;
        }
        return new AdicionalHistorialDTO(
                adicional.getNombre(),
                adicional.getCantidad() > 0 ? adicional.getCantidad() : 1
        );
    }
}
