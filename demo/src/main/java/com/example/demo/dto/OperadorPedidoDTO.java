package com.example.demo.dto;

import com.example.demo.entities.Operador;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO resumido con el id y nombre del operador asociado a un pedido o domiciliario
@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_operador", "nombre"})
public class OperadorPedidoDTO {

    @JsonProperty("id_operador")
    private Long id_operador;

    @JsonProperty("nombre")
    private String nombre;

    // Constructor con id y nombre del operador
    public OperadorPedidoDTO(Long id_operador, String nombre) {
        this.id_operador = id_operador;
        this.nombre = nombre;
    }

    // Convierte una entidad Operador a OperadorPedidoDTO
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
