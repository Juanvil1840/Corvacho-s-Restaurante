package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Pedido;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO con toda la informacion detallada de un pedido, incluyendo cliente, operador, domiciliario e items
@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_Pedido", "estado", "fechaCreacion", "fechaEntrega", "cliente", "operador", "domiciliario", "detallePedidos"})
public class PedidoDTO {

    @JsonProperty("id_Pedido")
    private Long id_Pedido;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;

    @JsonProperty("fechaEntrega")
    private LocalDateTime fechaEntrega;

    @JsonProperty("cliente")
    private ClientePedidoDTO cliente;

    @JsonProperty("operador")
    private OperadorPedidoDTO operador;

    @JsonProperty("domiciliario")
    private DomiciliarioPedidoDTO domiciliario;

    @JsonProperty("detallePedidos")
    private List<DetallePedidoDTO> detallePedidos;

    // Constructor completo de PedidoDTO
    public PedidoDTO(Long id_Pedido, String estado, LocalDateTime fechaCreacion, LocalDateTime fechaEntrega,
                     ClientePedidoDTO cliente, OperadorPedidoDTO operador, DomiciliarioPedidoDTO domiciliario,
                     List<DetallePedidoDTO> detallePedidos) {
        this.id_Pedido = id_Pedido;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaEntrega = fechaEntrega;
        this.cliente = cliente;
        this.operador = operador;
        this.domiciliario = domiciliario;
        this.detallePedidos = detallePedidos;
    }

    // Convierte una entidad Pedido a PedidoDTO tomando sus propios detalles
    public static PedidoDTO fromEntity(Pedido pedido) {
        return fromEntity(pedido, pedido != null ? pedido.getDetallePedidos() : null);
    }

    // Convierte una entidad Pedido y una lista de detalles especifica a PedidoDTO
    public static PedidoDTO fromEntity(Pedido pedido, List<DetallePedido> detalles) {
        if (pedido == null) {
            return null;
        }

        List<DetallePedidoDTO> detallesDto;
        if (detalles != null) {
            detallesDto = detalles.stream()
                    .map(DetallePedidoDTO::fromEntity)
                    .toList();
        } else if (pedido.getDetallePedidos() != null) {
            detallesDto = pedido.getDetallePedidos().stream()
                    .map(DetallePedidoDTO::fromEntity)
                    .toList();
        } else {
            detallesDto = Collections.emptyList();
        }

        return new PedidoDTO(
                pedido.getId_Pedido(),
                pedido.getEstado(),
                pedido.getFechaCreacion(),
                pedido.getFechaEntrega(),
                ClientePedidoDTO.fromEntity(pedido.getCliente()),
                OperadorPedidoDTO.fromEntity(pedido.getOperador()),
                DomiciliarioPedidoDTO.fromEntity(pedido.getDomiciliario()),
                detallesDto
        );
    }
}
