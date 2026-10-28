package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.entities.Pedido;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO optimizado para el listado de pedidos en tablas (evita over-fetching de detalles de productos)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id_Pedido", "estado", "fechaCreacion", "cliente", "operador", "domiciliario"})
public class PedidoListaDTO {

    @JsonProperty("id_Pedido")
    private Long id_Pedido;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;

    @JsonProperty("cliente")
    private ClientePedidoDTO cliente;

    @JsonProperty("operador")
    private OperadorPedidoDTO operador;

    @JsonProperty("domiciliario")
    private DomiciliarioPedidoDTO domiciliario;

    public static PedidoListaDTO fromEntity(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        return new PedidoListaDTO(
                pedido.getId_Pedido(),
                pedido.getEstado(),
                pedido.getFechaCreacion(),
                ClientePedidoDTO.fromEntity(pedido.getCliente()),
                OperadorPedidoDTO.fromEntity(pedido.getOperador()),
                DomiciliarioPedidoDTO.fromEntity(pedido.getDomiciliario())
        );
    }
}

