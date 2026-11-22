package com.example.demo.service;

import java.time.Duration;
import java.util.Collection;
import java.util.OptionalDouble;

import com.example.demo.entities.Pedido;

// Calculo compartido del tiempo de entrega (lo usan los reportes y el seguimiento del pedido)
public final class TiemposEntrega {

    private static final String ENTREGADO = "Entregado";
    // Se descartan datos imposibles: entregas "antes" de crearse o de mas de un dia
    private static final long MAXIMO_MINUTOS = 24 * 60;

    private TiemposEntrega() {
    }

    // Promedio de minutos entre que se crea el pedido y se entrega (solo pedidos entregados)
    public static OptionalDouble promedioMinutos(Collection<Pedido> pedidos) {
        return pedidos.stream()
                .filter(p -> ENTREGADO.equalsIgnoreCase(p.getEstado()))
                .filter(p -> p.getFechaCreacion() != null && p.getFechaEntrega() != null)
                .mapToLong(p -> Duration.between(p.getFechaCreacion(), p.getFechaEntrega()).toMinutes())
                .filter(minutos -> minutos > 0 && minutos < MAXIMO_MINUTOS)
                .average();
    }
}
