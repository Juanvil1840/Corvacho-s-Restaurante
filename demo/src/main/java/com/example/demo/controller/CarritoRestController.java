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

@RestController
@RequestMapping("/api/carritos")
@CrossOrigin(origins = "http://localhost:4200")
public class CarritoRestController {

    private final CarritoService carritoService;

    public CarritoRestController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/cliente/{clienteId}")
    public CarritoDTO obtenerPorCliente(@PathVariable Long clienteId) {
        return carritoService.obtenerPorCliente(clienteId);
    }

    @PutMapping("/cliente/{clienteId}")
    public CarritoDTO actualizarPorCliente(@PathVariable Long clienteId,
            @Valid @RequestBody CarritoDTO carrito) {
        return carritoService.actualizarPorCliente(clienteId, carrito);
    }

    @DeleteMapping("/cliente/{clienteId}")
    public ResponseEntity<Void> vaciarPorCliente(@PathVariable Long clienteId) {
        carritoService.vaciarPorCliente(clienteId);
        return ResponseEntity.noContent().build();
    }
}
