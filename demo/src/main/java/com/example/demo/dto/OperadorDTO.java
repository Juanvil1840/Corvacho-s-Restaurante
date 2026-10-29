package com.example.demo.dto;

import com.example.demo.entities.Operador;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_operador", "nombre", "usuario"})
public class OperadorDTO {

    @JsonProperty("id_operador")
    private Long id_operador;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("usuario")
    private String usuario;

    public OperadorDTO(Long id_operador, String nombre, String usuario) {
        this.id_operador = id_operador;
        this.nombre = nombre;
        this.usuario = usuario;
    }

    // No se envia la contrasena al frontend
    public static OperadorDTO fromEntity(Operador operador) {
        if (operador == null) {
            return null;
        }
        return new OperadorDTO(
                operador.getId_operador(),
                operador.getNombre(),
                operador.getUsuario()
        );
    }
}
