package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"mensaje"})
public class MensajeDTO {

    @JsonProperty("mensaje")
    private String mensaje;

    public MensajeDTO(String mensaje) {
        this.mensaje = mensaje;
    }
}
