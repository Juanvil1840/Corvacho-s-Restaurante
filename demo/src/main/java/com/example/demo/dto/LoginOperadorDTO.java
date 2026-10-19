package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_operador", "nombre", "usuario"})
public class LoginOperadorDTO {

    @JsonProperty("id_operador")
    private Long id_operador;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("usuario")
    private String usuario;

    public LoginOperadorDTO(Long id_operador, String nombre, String usuario) {
        this.id_operador = id_operador;
        this.nombre = nombre;
        this.usuario = usuario;
    }
}