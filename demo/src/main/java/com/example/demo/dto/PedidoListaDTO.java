package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.entities.Pedido;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para la tabla de pedidos: solo los datos que se muestran en cada fila
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id_Pedido", "estado", "fechaCreacion", "clienteNombre", "operadorNombre", "domiciliarioNombre"})
public class PedidoListaDTO {

    @JsonProperty("id_Pedido")
    private Long id_Pedido;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;

    @JsonProperty("clienteNombre")
    private String clienteNombre;

    @JsonProperty("operadorNombre")
    private String operadorNombre;

    @JsonProperty("domiciliarioNombre")
    private String domiciliarioNombre;

    // Convierte una entidad Pedido en una fila resumida para el listado de pedidos
    public static PedidoListaDTO fromEntity(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        String clienteNombre = null;
        if (pedido.getCliente() != null) {
            clienteNombre = pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido();
        }
        String operadorNombre = pedido.getOperador() != null ? pedido.getOperador().getNombre() : null;
        String domiciliarioNombre = pedido.getDomiciliario() != null ? pedido.getDomiciliario().getNombre() : null;
        return new PedidoListaDTO(
                pedido.getId_Pedido(),
                pedido.getEstado(),
                pedido.getFechaCreacion(),
                clienteNombre,
                operadorNombre,
                domiciliarioNombre
        );
    }
}
