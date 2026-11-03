package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CarritoDTO;
import com.example.demo.service.CarritoService;

import jakarta.validation.Valid;

// Controlador REST para la gestion del carrito de compras de cada cliente
@RestController
@RequestMapping("/api/carritos")
@CrossOrigin(origins = "http://localhost:4200")
public class CarritoRestController {

    private final CarritoService carritoService;

    // Constructor para inyectar el servicio de carritos
    public CarritoRestController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    // Obtiene el carrito actual de un cliente especifico
    @GetMapping("/cliente/{clienteId}")
    public CarritoDTO obtenerPorCliente(@PathVariable Long clienteId) {
        return carritoService.obtenerPorCliente(clienteId);
    }

    // Reemplaza y actualiza los productos y adicionales del carrito de un cliente
    @PutMapping("/cliente/{clienteId}")
    public CarritoDTO actualizarPorCliente(@PathVariable Long clienteId,
            @Valid @RequestBody CarritoDTO carrito) {
        return carritoService.actualizarPorCliente(clienteId, carrito);
    }

    // Vacia todos los productos del carrito de un cliente
    @DeleteMapping("/cliente/{clienteId}")
    public ResponseEntity<Void> vaciarPorCliente(@PathVariable Long clienteId) {
        carritoService.vaciarPorCliente(clienteId);
        return ResponseEntity.noContent().build();
    }
}
