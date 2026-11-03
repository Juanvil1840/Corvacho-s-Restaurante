package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para recibir las credenciales de inicio de sesion de un cliente
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginClienteDTO {
    private String correo;

    private String contrasena;
}
