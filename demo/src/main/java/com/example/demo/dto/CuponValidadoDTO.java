package com.example.demo.dto;

// Resultado de probar un cupon en el carrito: cuanto descuenta y como queda el total
public record CuponValidadoDTO(
        String codigo,
        String descripcion,
        String tipo,
        double valor,
        double subtotal,
        double descuento,
        double total) {
}
