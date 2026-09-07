package com.example.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor  
public class Cliente {

    private Long clienteId;
    private String nombre;
    private String apellido;
    private String correo;
    private String contraseña;
    private String telefono;
    private String direccion;
    private boolean activo = true;  // Por defecto, el cliente esta activo
    
}
