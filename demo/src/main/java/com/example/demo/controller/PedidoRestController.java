package com.example.demo.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PedidoDTO;
import com.example.demo.dto.PedidoHistorialDTO;
import com.example.demo.dto.PedidoListaDTO;
import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Pedido;
import com.example.demo.service.PedidoService;

// Controlador REST para la gestion de Pedidos
@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoRestController {

    private final PedidoService pedidoService;

    // Constructor para inyectar el servicio de pedidos
    public PedidoRestController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Lista los pedidos registrados (con activos=true no trae los entregados)
    @GetMapping
    public Collection<PedidoListaDTO> listar(
            @RequestParam(value = "activos", required = false, defaultValue = "false") boolean activos) {
        Collection<Pedido> pedidos = activos ? pedidoService.findActivos() : pedidoService.findAll();
        return pedidos.stream()
                .map(PedidoListaDTO::fromEntity)
                .toList();
    }

    // Lista el historial de pedidos de un cliente especifico
    @GetMapping("/cliente/{clienteId}")
    public Collection<PedidoHistorialDTO> listarPorCliente(@PathVariable Long clienteId) {
        return pedidoService.findByClienteId(clienteId)
                .stream()
                .map(p -> PedidoHistorialDTO.fromEntity(p, pedidoService.findDetallesByPedidoId(p.getId_Pedido())))
                .toList();
    }

    // Busca un pedido por su id y devuelve su detalle completo
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirPedido(pedido));
    }

    // Crea un nuevo pedido en la base de datos
    @PostMapping
    public ResponseEntity<PedidoDTO> crear(@RequestBody Pedido pedido) {
        Pedido guardado = pedidoService.crearPedido(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirPedido(guardado));
    }

    // Actualiza los datos de un pedido existente
    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> actualizar(@PathVariable Long id, @RequestBody Pedido pedido) {
        Pedido actualizado = pedidoService.actualizarPedido(id, pedido);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirPedido(actualizado));
    }

    // Elimina un pedido de la base de datos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (pedidoService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        pedidoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Convertir la entidad Pedido a PedidoDTO
    private PedidoDTO convertirPedido(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        List<DetallePedido> detalles = pedidoService.findDetallesByPedidoId(pedido.getId_Pedido());
        return PedidoDTO.fromEntity(pedido, detalles);
    }
}
