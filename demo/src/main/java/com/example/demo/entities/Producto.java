package com.example.demo.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor      
public class Producto {
    private Integer id;
    private String nombre;
    private double precio;
    private String descripcion;
    private String imagen;
    private boolean disponible;
    private List<String> ingredientes;
}