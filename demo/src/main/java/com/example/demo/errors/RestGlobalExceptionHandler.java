package com.example.demo.errors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.MensajeDTO;

// Manejador global de excepciones para los controladores REST
@RestControllerAdvice
public class RestGlobalExceptionHandler {

    // Captura las excepciones de estado HTTP y devuelve el mensaje en formato JSON usando MensajeDTO
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<MensajeDTO> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(new MensajeDTO(ex.getReason() != null ? ex.getReason() : ex.getMessage()));
    }
}
