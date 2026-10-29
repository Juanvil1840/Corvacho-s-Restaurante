package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;


@Data
@AllArgsConstructor
@NoArgsConstructor 
@Entity
public class Adicional{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id_adicional;
    @Column( length = 80, nullable = false)
    private String nombre;
    @Column( nullable = false)
    private double precio;
    @Column( nullable = false)
    private boolean disponible;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    public Adicional(String nombre, double precio, boolean disponible){
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
    }

    public Adicional(String nombre, double precio, boolean disponible, Categoria categoria){
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
        this.categoria = categoria;
    }
}
