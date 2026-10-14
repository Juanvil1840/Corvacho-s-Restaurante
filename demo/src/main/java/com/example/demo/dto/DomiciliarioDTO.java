package com.example.demo.dto;

import com.example.demo.entities.Domiciliario;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id_domiciliario", "nombre", "celular", "cedula", "informaciondisp", "operador"})
public class DomiciliarioDTO {

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

    @JsonProperty("operador")
    private OperadorPedidoDTO operador;

    public DomiciliarioDTO(Long id_domiciliario, String nombre, String celular, String cedula, boolean informaciondisp, OperadorPedidoDTO operador) {
        this.id_domiciliario = id_domiciliario;
        this.nombre = nombre;
        this.celular = celular;
        this.cedula = cedula;
        this.informaciondisp = informaciondisp;
        this.operador = operador;
    }

    public static DomiciliarioDTO fromEntity(Domiciliario domiciliario) {
        if (domiciliario == null) {
            return null;
        }
        return new DomiciliarioDTO(
                domiciliario.getId_domiciliario(),
                domiciliario.getNombre(),
                domiciliario.getCelular(),
                domiciliario.getCedula(),
                domiciliario.isInformaciondisp(),
                OperadorPedidoDTO.fromEntity(domiciliario.getOperador())
        );
    }
}
