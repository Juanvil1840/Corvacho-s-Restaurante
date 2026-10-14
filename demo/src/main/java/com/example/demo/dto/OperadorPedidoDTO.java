package com.example.demo.dto;

import com.example.demo.entities.Operador;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_operador", "nombre"})
public class OperadorPedidoDTO {

    @JsonProperty("id_operador")
    private Long id_operador;

    @JsonProperty("nombre")
    private String nombre;

    public OperadorPedidoDTO(Long id_operador, String nombre) {
        this.id_operador = id_operador;
        this.nombre = nombre;
    }

    public static OperadorPedidoDTO fromEntity(Operador operador) {
        if (operador == null) {
            return null;
        }
        return new OperadorPedidoDTO(
                operador.getId_operador(),
                operador.getNombre()
        );
    }
}
