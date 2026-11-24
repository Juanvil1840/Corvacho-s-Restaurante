package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearResenaDTO(
        @NotNull @Min(1) @Max(5) Integer puntuacion,
        @Size(max = 1000) String comentario) {
}
