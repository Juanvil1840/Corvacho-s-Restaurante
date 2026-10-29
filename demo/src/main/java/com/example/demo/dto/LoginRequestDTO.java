package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para recibir las credenciales de login de administradores y operadores
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    private String usuario;
    private String contrasena;
}
