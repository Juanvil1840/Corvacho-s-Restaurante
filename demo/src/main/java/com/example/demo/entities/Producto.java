package com.example.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor    
@NoArgsConstructor      
public class Producto {
    private String nombre;
    private double precio;
    private String descripcion;
    private String imagen;
    private boolean disponible;

}
