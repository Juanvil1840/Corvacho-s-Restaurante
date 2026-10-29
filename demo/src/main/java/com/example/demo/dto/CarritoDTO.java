package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Se usa para leer el carrito y tambien para recibir los cambios desde Angular
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {

    @NotNull
    @Valid
    private List<ProductoCarritoDTO> items = new ArrayList<>();
}
