package com.example.demo.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor      
public class Producto { //Creamos una clase Producto con sus atributos, id,nombre,precio,descripcion, imagen, si esta disponile, los ingredientes y su categoria
    private Long id;
    private String nombre;
    private double precio;
    private String descripcion;
    private String imagen;
    private boolean disponible;
    private List<String> ingredientes;
    private Integer idCategoria;
}