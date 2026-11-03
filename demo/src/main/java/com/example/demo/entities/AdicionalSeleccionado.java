package com.example.demo.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Objeto embebible que guarda los datos y cantidad de un adicional elegido en un pedido o carrito
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

