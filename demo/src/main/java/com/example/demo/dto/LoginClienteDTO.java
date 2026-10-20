package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginClienteDTO {
    private String correo;

    @JsonAlias({"contraseña"})
    private String contrasena;
}
