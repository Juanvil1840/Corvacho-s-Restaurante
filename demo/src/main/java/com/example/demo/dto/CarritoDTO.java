package com.example.demo.dto;

import java.util.List;

public record CarritoDTO(List<Item> items) {
    public record Item(Long productoId, String nombre, double precio, String imagen,
            int cantidad, List<Adicional> adicionales) {
    }

    public record Adicional(Long id_adicional, String nombre, double precio, int cantidad) {
    }
}
