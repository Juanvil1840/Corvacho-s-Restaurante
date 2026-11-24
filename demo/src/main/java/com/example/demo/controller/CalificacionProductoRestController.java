package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CalificacionProductoDTO;
import com.example.demo.dto.CrearCalificacionProductoDTO;
import com.example.demo.service.CalificacionProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/calificaciones-productos")
public class CalificacionProductoRestController {
    private final CalificacionProductoService calificacionService;

    public CalificacionProductoRestController(CalificacionProductoService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @PostMapping("/pedido/{pedidoId}/producto/{productoId}")
    @PreAuthorize("@seguridad.esPedidoDelCliente(#pedidoId)")
    public ResponseEntity<CalificacionProductoDTO> crear(
            @PathVariable Long pedidoId,
            @PathVariable Long productoId,
            @Valid @RequestBody CrearCalificacionProductoDTO solicitud) {
        var creada = calificacionService.crear(pedidoId, productoId, solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(CalificacionProductoDTO.fromEntity(creada));
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("@seguridad.esElCliente(#clienteId)")
    public List<CalificacionProductoDTO> porCliente(@PathVariable Long clienteId) {
        return calificacionService.porCliente(clienteId).stream()
                .map(CalificacionProductoDTO::fromEntity).toList();
    }
}
