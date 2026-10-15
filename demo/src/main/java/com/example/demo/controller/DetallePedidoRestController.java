package com.example.demo.controller;

import java.util.Collection;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.DetallePedidoDTO;
import com.example.demo.service.DetallePedidoService;

@RestController
@RequestMapping("/api/detalles-pedido")
@CrossOrigin(origins = "http://localhost:4200")
public class DetallePedidoRestController {

    private final DetallePedidoService detallePedidoService;

    public DetallePedidoRestController(DetallePedidoService detallePedidoService) {
        this.detallePedidoService = detallePedidoService;
    }

    @GetMapping
    public Collection<DetallePedidoDTO> listar() {
        return detallePedidoService.findAll()
                .stream()
                .map(DetallePedidoDTO::fromEntity)
                .toList();
    }

    @GetMapping("/pedido/{pedidoId}")
    public Collection<DetallePedidoDTO> listarPorPedido(@PathVariable Long pedidoId) {
        return detallePedidoService.findAll()
                .stream()
                .filter(d -> d.getPedido() != null && pedidoId.equals(d.getPedido().getId_Pedido()))
                .map(DetallePedidoDTO::fromEntity)
                .toList();
    }
}

