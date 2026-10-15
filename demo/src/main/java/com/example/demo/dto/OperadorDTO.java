package com.example.demo.dto;

import com.example.demo.entities.Operador;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_operador", "nombre", "usuario", "contrasena"})
public class OperadorDTO {

    @JsonProperty("id_operador")
    private Long id_operador;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("usuario")
    private String usuario;

    @JsonProperty("contrasena")
    private String contrasena;

    public OperadorDTO(Long id_operador, String nombre, String usuario, String contrasena) {
        this.id_operador = id_operador;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public static OperadorDTO fromEntity(Operador operador) {
        if (operador == null) {
            return null;
        }
        return new OperadorDTO(
                operador.getId_operador(),
                operador.getNombre(),
                operador.getUsuario(),
                operador.getContrasena()
        );
    }
}
