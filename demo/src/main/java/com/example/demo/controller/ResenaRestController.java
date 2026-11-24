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

import com.example.demo.dto.CrearResenaDTO;
import com.example.demo.dto.ResenaDTO;
import com.example.demo.service.ResenaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/resenas")
public class ResenaRestController {
    private final ResenaService resenaService;

    public ResenaRestController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @PostMapping("/pedido/{pedidoId}")
    @PreAuthorize("@seguridad.esPedidoDelCliente(#pedidoId)")
    public ResponseEntity<ResenaDTO> crear(
            @PathVariable Long pedidoId, @Valid @RequestBody CrearResenaDTO solicitud) {
        var creada = resenaService.crear(pedidoId, solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResenaDTO.fromEntity(creada));
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("@seguridad.esElCliente(#clienteId)")
    public List<ResenaDTO> porCliente(@PathVariable Long clienteId) {
        return resenaService.porCliente(clienteId).stream().map(ResenaDTO::fromEntity).toList();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ResenaDTO> todas() {
        return resenaService.todas().stream().map(ResenaDTO::fromEntity).toList();
    }
}
