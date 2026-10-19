package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.Collection;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.PedidoDTO;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Operador;
import com.example.demo.entities.Pedido;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoRestController {

    private final PedidoService pedidoService;
    private final ClienteRepository clienteRepository;
    private final OperadorRepository operadorRepository;
    private final DomiciliarioRepository domiciliarioRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public PedidoRestController(
            PedidoService pedidoService,
            ClienteRepository clienteRepository,
            OperadorRepository operadorRepository,
            DomiciliarioRepository domiciliarioRepository,
            ProductoRepository productoRepository,
            DetallePedidoRepository detallePedidoRepository) {
        this.pedidoService = pedidoService;
        this.clienteRepository = clienteRepository;
        this.operadorRepository = operadorRepository;
        this.domiciliarioRepository = domiciliarioRepository;
        this.productoRepository = productoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }
   
    @GetMapping
    public Collection<PedidoDTO> listar() {
        return pedidoService.findAll()
                .stream()
                .map(this::convertirPedido)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.findById(id);
            if (pedido == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(convertirPedido(pedido));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> crear(@RequestBody Pedido pedido) {
        pedido.setId_Pedido(null);
        pedido.setCliente(buscarCliente(pedido));
        pedido.setOperador(buscarOperador(pedido));
        pedido.setDomiciliario(buscarDomiciliario(pedido));
        if (pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(LocalDateTime.now());
        }
        if (pedido.getEstado() == null || pedido.getEstado().trim().isEmpty()) {
            pedido.setEstado("Recibido");
        }

        java.util.List<DetallePedido> detallesRecibidos = pedido.getDetallePedidos();
        pedido.setDetallePedidos(null);

        Pedido guardado = pedidoService.guardar(pedido);

        if (detallesRecibidos != null && !detallesRecibidos.isEmpty()) {
            for (DetallePedido d : detallesRecibidos) {
                if (d.getProducto() != null && d.getProducto().getId() != null) {
                    productoRepository.findById(d.getProducto().getId()).ifPresent(prod -> {
                        DetallePedido nuevo = new DetallePedido();
                        nuevo.setPedido(guardado);
                        nuevo.setProducto(prod);
                        nuevo.setCantidad(d.getCantidad() > 0 ? d.getCantidad() : 1);
                        detallePedidoRepository.save(nuevo);
                    });
                }
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(convertirPedido(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Pedido pedido) {
        Pedido existente;
        try {
            existente = pedidoService.findById(id);
            if (existente == null) {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

        existente.setEstado(pedido.getEstado() != null ? pedido.getEstado() : existente.getEstado());
        existente.setCliente(buscarCliente(pedido));
        existente.setOperador(buscarOperador(pedido));
        existente.setDomiciliario(buscarDomiciliario(pedido));
        if (pedido.getFechaCreacion() != null) {
            existente.setFechaCreacion(pedido.getFechaCreacion());
        }
        existente.setFechaEntrega(pedido.getFechaEntrega());

        Pedido actualizado = pedidoService.guardar(existente);

        if (pedido.getDetallePedidos() != null) {
            java.util.List<DetallePedido> previos = detallePedidoRepository.findByPedido_Id_Pedido(id);
            if (!previos.isEmpty()) {
                detallePedidoRepository.deleteAll(previos);
            }
            for (DetallePedido d : pedido.getDetallePedidos()) {
                if (d.getProducto() != null && d.getProducto().getId() != null) {
                    productoRepository.findById(d.getProducto().getId()).ifPresent(prod -> {
                        DetallePedido nuevo = new DetallePedido();
                        nuevo.setPedido(actualizado);
                        nuevo.setProducto(prod);
                        nuevo.setCantidad(d.getCantidad() > 0 ? d.getCantidad() : 1);
                        detallePedidoRepository.save(nuevo);
                    });
                }
            }
        }

        return ResponseEntity.ok(convertirPedido(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            Pedido existente = pedidoService.findById(id);
            if (existente == null) {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

        pedidoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Cliente buscarCliente(Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getClienteId() == null
                || pedido.getCliente().getClienteId() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un cliente");
        }
        return clienteRepository.findById(pedido.getCliente().getClienteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El cliente seleccionado no existe"));
    }

    private Operador buscarOperador(Pedido pedido) {
        if (pedido.getOperador() == null || pedido.getOperador().getId_operador() == null
                || pedido.getOperador().getId_operador() <= 0) {
            return null;
        }
        return operadorRepository.findById(pedido.getOperador().getId_operador())
                .orElse(null);
    }

    private Domiciliario buscarDomiciliario(Pedido pedido) {
        if (pedido.getDomiciliario() == null || pedido.getDomiciliario().getId_domiciliario() == null
                || pedido.getDomiciliario().getId_domiciliario() <= 0) {
            return null;
        }
        return domiciliarioRepository.findById(pedido.getDomiciliario().getId_domiciliario())
                .orElse(null);
    }

    private PedidoDTO convertirPedido(Pedido pedido) {
        java.util.List<DetallePedido> detalles = pedido.getId_Pedido() != null
                ? detallePedidoRepository.findByPedido_Id_Pedido(pedido.getId_Pedido())
                : java.util.Collections.emptyList();
        return PedidoDTO.fromEntity(pedido, detalles);
    }
    
}
