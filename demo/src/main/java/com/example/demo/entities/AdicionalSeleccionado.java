package com.example.demo.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AdicionalSeleccionado {
    private Long id_adicional;
    private String nombre;
    private double precio;
    private int cantidad = 1;
}

