package com.example.demo.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// Entidad que representa una categoria del menu (ej. Pastas, Carnes, Bebidas)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity 
public class Categoria {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80, nullable = false)
    private String nombre;

    // Productos que pertenecen a esta categoria (Evitamos bucle infinito con @JsonIgnore)
    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<Producto> productos;

    // Adicionales permitidos para esta categoria (Evitamos bucle infinito con @JsonIgnore)
    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "categoria")
    private List<Adicional> adicionales;

    // Constructor con el nombre de la categoria
    public Categoria(String nombre) {
        this.nombre = nombre;
    }
}