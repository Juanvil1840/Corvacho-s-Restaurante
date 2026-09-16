package com.example.demo.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    @Column( precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;
    @Column(nullable = false)
    private boolean disponible;

    
}
