package com.example.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.ToString;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity 
public class Categoria{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column( length = 80, nullable = false)
    private String nombre;

    @ToString.Exclude
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<Producto> productos;

    public Categoria(String nombre) {
    this.nombre = nombre;
    }

}
