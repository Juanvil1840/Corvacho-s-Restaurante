package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.DetallePedido;

// Calculo del valor de las lineas de un pedido: (precio del plato + adicionales) x cantidad.
// Es el mismo calculo que usan el historial del cliente y los reportes
public final class PreciosPedido {

    private PreciosPedido() {
    }

    public static double subtotal(Collection<DetallePedido> detalles) {
        if (detalles == null) {
            return 0;
        }
        return detalles.stream().mapToDouble(PreciosPedido::subtotal).sum();
    }

    public static double subtotal(DetallePedido detalle) {
        double precioBase = detalle.getProducto() != null ? detalle.getProducto().getPrecio() : 0;
        return (precioBase + extrasPorUnidad(detalle)) * Math.max(detalle.getCantidad(), 1);
    }

    public static double extrasPorUnidad(DetallePedido detalle) {
        if (detalle.getAdicionales() == null) {
            return 0;
        }
        return detalle.getAdicionales().stream()
                .mapToDouble(a -> a.getPrecio() * Math.max(a.getCantidad(), 1))
                .sum();
    }
}
