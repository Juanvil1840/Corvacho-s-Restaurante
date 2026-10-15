package com.example.demo.dto;

import com.example.demo.entities.Cliente;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"clienteId", "nombre", "apellido", "correo", "telefono", "direccion", "activo"})
public class ClienteDTO {

    @JsonProperty("clienteId")
    private Long clienteId;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellido")
    private String apellido;

    @JsonProperty("correo")
    private String correo;

    @JsonProperty("telefono")
    private String telefono;

    @JsonProperty("direccion")
    private String direccion;

    @JsonProperty("activo")
    private boolean activo;

    public ClienteDTO(Long clienteId, String nombre, String apellido, String correo, String telefono, String direccion, boolean activo) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = activo;
    }

    public static ClienteDTO fromEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return new ClienteDTO(
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getCorreo() != null ? cliente.getCorreo() : "",
                cliente.getTelefono() != null ? cliente.getTelefono() : "",
                cliente.getDireccion() != null ? cliente.getDireccion() : "",
                cliente.isActivo()
        );
    }
}
