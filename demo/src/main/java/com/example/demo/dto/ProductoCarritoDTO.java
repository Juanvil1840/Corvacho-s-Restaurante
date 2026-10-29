package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Un producto dentro del carrito con sus adicionales
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCarritoDTO {

    @NotNull
    private Long productoId;

    private String nombre;

    private double precio;

    private String imagen;

    @Min(1)
    private int cantidad;

    @Valid
    private List<AdicionalPedidoDTO> adicionales = new ArrayList<>();
}
