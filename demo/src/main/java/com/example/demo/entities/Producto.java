package com.example.demo.entities;

/* import java.util.List; */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Producto {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Creamos una clase Producto con sus atributos, id,nombre,precio,descripcion,       // imagen, si esta disponile, los ingredientes y su categoria
    private Long id;
    @Column( length = 80, nullable = false)
    private String nombre;
    @Column(nullable = false)
    private double precio;
    @Column( columnDefinition = "TEXT", nullable = false)
    private String descripcion;
    @Column( length = 255, nullable = false)
    private String imagen;
    @Column( nullable = false)
    private boolean disponible;
    /* Quitar ingredientes 
    private List<String> ingredientes; */
    @ManyToOne
    @JoinColumn( name = "id_categoria", nullable = false) 
    private Categoria categoria;

    public Producto(String nombre, double precio, String descripcion, String imagen, boolean disponible, Categoria categoria) {
    this.nombre = nombre;
    this.precio = precio;
    this.descripcion = descripcion;
    this.imagen = imagen;
    this.disponible = disponible;
    this.categoria = categoria;
    }
}