package com.example.demo.controller;

import java.util.Collection;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.DetallePedidoDTO;
import com.example.demo.service.DetallePedidoService;

// Controlador REST para la consulta de Detalles de un pedido
@RestController
@RequestMapping("/api/detalles-pedido")
@CrossOrigin(origins = "http://localhost:4200")
public class DetallePedidoRestController {

    private final DetallePedidoService detallePedidoService;

    // Constructor para inyectar el servicio de detalles de pedido
    public DetallePedidoRestController(DetallePedidoService detallePedidoService) {
        this.detallePedidoService = detallePedidoService;
    }

    // Lista todos los detalles de pedido registrados en la base de datos
    @GetMapping
    public Collection<DetallePedidoDTO> listar() {
        return detallePedidoService.findAll()
                .stream()
                .map(DetallePedidoDTO::fromEntity)
                .toList();
    }

    // Lista los detalles asociados a un pedido especifico
    @GetMapping("/pedido/{pedidoId}")
    public Collection<DetallePedidoDTO> listarPorPedido(@PathVariable Long pedidoId) {
        return detallePedidoService.findByPedidoId(pedidoId)
                .stream()
                .map(DetallePedidoDTO::fromEntity)
                .toList();
    }
}