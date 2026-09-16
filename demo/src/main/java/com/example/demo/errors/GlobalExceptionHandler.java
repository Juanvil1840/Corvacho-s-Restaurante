package com.example.demo.errors;

import com.example.demo.errors.ClienteNotFoundException;
import com.example.demo.errors.ProductoNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
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

    // Maneja cualquier otro error inesperado
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("mensaje", "Ha ocurrido un error inesperado. Por favor, intentalo de nuevo.");
        return "errors/error";
    }
}