package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Respuesta del login: el token JWT, el rol y los datos publicos del usuario (ClienteDTO, OperadorDTO o AdministradorDTO)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"token", "rol", "usuario"})
public class AuthResponseDTO<T> {
    private String token;
    private String rol;
    private T usuario;
}
