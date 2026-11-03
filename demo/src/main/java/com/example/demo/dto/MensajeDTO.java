package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO sencillo para enviar mensajes de respuesta o error en formato JSON al frontend
@Data
@NoArgsConstructor
@JsonPropertyOrder({"mensaje"})
public class MensajeDTO {

    @JsonProperty("mensaje")
    private String mensaje;

    // Constructor que recibe el texto del mensaje
    public MensajeDTO(String mensaje) {
        this.mensaje = mensaje;
    }
}
