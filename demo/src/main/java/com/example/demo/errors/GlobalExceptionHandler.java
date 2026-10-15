package com.example.demo.errors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {

    // Maneja cuando un cliente no se encuentra
    @ExceptionHandler(ClienteNotFoundException.class)
    public String handleClienteNotFound(ClienteNotFoundException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "errors/error";
    }

    // Maneja cuando un producto no se encuentra
    @ExceptionHandler(ProductoNotFoundException.class)
    public String handleProductoNotFound(ProductoNotFoundException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "errors/error";
    }

    // Maneja cuando un pedido no se encuentra
    @ExceptionHandler(PedidoNotFoundException.class)
    public String handlePedidoNotFound(PedidoNotFoundException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "errors/error";
    }

    // Maneja ResponseStatusException devolviendo respuesta JSON adecuada
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public org.springframework.http.ResponseEntity<com.example.demo.dto.MensajeDTO> handleResponseStatus(
            org.springframework.web.server.ResponseStatusException ex) {
        return org.springframework.http.ResponseEntity.status(ex.getStatusCode())
                .body(new com.example.demo.dto.MensajeDTO(ex.getReason() != null ? ex.getReason() : ex.getMessage()));
    }

    // Maneja cualquier otro error inesperado
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("mensaje", "Ha ocurrido un error inesperado. Por favor, intentalo de nuevo.");
        return "errors/error";
    }
}