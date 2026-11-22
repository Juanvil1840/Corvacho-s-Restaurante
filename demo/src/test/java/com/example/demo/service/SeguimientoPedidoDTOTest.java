package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.demo.dto.SeguimientoPedidoDTO;
import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Pedido;

// Pruebas de lo que ve el cliente en el seguimiento en vivo de su pedido
class SeguimientoPedidoDTOTest {

    private static final LocalDateTime CREADO = LocalDateTime.of(2026, 11, 22, 19, 0);

    private final Domiciliario domiciliario = new Domiciliario("Juls Caicedo", "3056677848", "CC-1", true, null);

    // 1. Un pedido en cocina: paso 2, llegada estimada = creacion + promedio y sin datos del domiciliario
    @Test
    void pedidoCocinando_calculaLlegadaYOcultaDomiciliario() {
        Pedido pedido = pedido("cocinando");

        SeguimientoPedidoDTO dto = SeguimientoPedidoDTO.fromEntity(pedido, 42);

        assertThat(dto.estado()).isEqualTo("Cocinando");
        assertThat(dto.paso()).isEqualTo(2);
        assertThat(dto.llegadaEstimada()).isEqualTo(CREADO.plusMinutes(42));
        assertThat(dto.domiciliario()).isNull();
        assertThat(dto.celularDomiciliario()).isNull();
    }

    // 2. Cuando el pedido sale del restaurante el cliente ya ve quien se lo lleva
    @Test
    void pedidoEnviado_muestraDomiciliario() {
        SeguimientoPedidoDTO dto = SeguimientoPedidoDTO.fromEntity(pedido("Enviado"), 42);

        assertThat(dto.paso()).isEqualTo(3);
        assertThat(dto.domiciliario()).isEqualTo("Juls Caicedo");
        assertThat(dto.celularDomiciliario()).isEqualTo("3056677848");
    }

    // 3. Un pedido entregado ya no tiene hora estimada
    @Test
    void pedidoEntregado_sinLlegadaEstimada() {
        Pedido pedido = pedido("Entregado");
        pedido.setFechaEntrega(CREADO.plusMinutes(35));

        SeguimientoPedidoDTO dto = SeguimientoPedidoDTO.fromEntity(pedido, 42);

        assertThat(dto.paso()).isEqualTo(4);
        assertThat(dto.llegadaEstimada()).isNull();
        assertThat(dto.fechaEntrega()).isEqualTo(CREADO.plusMinutes(35));
    }

    private Pedido pedido(String estado) {
        Pedido pedido = new Pedido(estado, CREADO, null, null, null, domiciliario);
        pedido.setId_Pedido(7L);
        return pedido;
    }
}
