package com.example.demo.dto;

import com.example.demo.entities.Domiciliario;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_domiciliario", "nombre", "celular", "cedula", "informaciondisp"})
public class DomiciliarioPedidoDTO {

    @JsonProperty("id_domiciliario")
    private Long id_domiciliario;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("celular")
    private String celular;

    @JsonProperty("cedula")
    private String cedula;

    @JsonProperty("informaciondisp")
    private boolean informaciondisp;

    public DomiciliarioPedidoDTO(Long id_domiciliario, String nombre, String celular, String cedula, boolean informaciondisp) {
        this.id_domiciliario = id_domiciliario;
        this.nombre = nombre;
        this.celular = celular;
        this.cedula = cedula;
        this.informaciondisp = informaciondisp;
    }

    public static DomiciliarioPedidoDTO fromEntity(Domiciliario domiciliario) {
        if (domiciliario == null) {
            return null;
        }
        return new DomiciliarioPedidoDTO(
                domiciliario.getId_domiciliario(),
                domiciliario.getNombre(),
                domiciliario.getCelular() != null ? domiciliario.getCelular() : "",
                domiciliario.getCedula() != null ? domiciliario.getCedula() : "",
                domiciliario.isInformaciondisp()
        );
    }
}
