package com.example.demo.dto;

import com.example.demo.entities.Cliente;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Datos del cliente que se necesitan para entregar un pedido
@Data
@NoArgsConstructor
@JsonPropertyOrder({"clienteId", "nombre", "apellido", "telefono", "direccion"})
public class ClientePedidoDTO {

    @JsonProperty("clienteId")
    private Long clienteId;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellido")
    private String apellido;

    @JsonProperty("telefono")
    private String telefono;

    @JsonProperty("direccion")
    private String direccion;

    public ClientePedidoDTO(Long clienteId, String nombre, String apellido, String telefono, String direccion) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public static ClientePedidoDTO fromEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return new ClientePedidoDTO(
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono() != null ? cliente.getTelefono() : "",
                cliente.getDireccion() != null ? cliente.getDireccion() : ""
        );
    }
}
