package com.example.demo.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ActualizarCarritoDTO(@NotNull List<@NotNull @Valid Item> items) {
    public record Item(
            @NotNull @Positive Long productoId,
            @Min(1) int cantidad,
            @NotNull List<@NotNull @Valid Adicional> adicionales) {
    }

    public record Adicional(@NotNull @Positive Long id_adicional, @Min(1) int cantidad) {
    }
}
