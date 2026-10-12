package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

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

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Operador;
import com.example.demo.entities.Pedido;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoRestController {

    private final PedidoService pedidoService;
    private final ClienteRepository clienteRepository;
    private final OperadorRepository operadorRepository;
    private final DomiciliarioRepository domiciliarioRepository;

    public PedidoRestController(
            PedidoService pedidoService,
            ClienteRepository clienteRepository,
            OperadorRepository operadorRepository,
            DomiciliarioRepository domiciliarioRepository) {
        this.pedidoService = pedidoService;
        this.clienteRepository = clienteRepository;
        this.operadorRepository = operadorRepository;
        this.domiciliarioRepository = domiciliarioRepository;
    }

    @GetMapping
    public Collection<Map<String, Object>> listar() {
        return pedidoService.findAll()
                .stream()
                .map(this::convertirPedido)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
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
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Pedido pedido) {
        pedido.setId_Pedido(null);
        pedido.setCliente(buscarCliente(pedido));
        pedido.setOperador(buscarOperador(pedido));
        pedido.setDomiciliario(buscarDomiciliario(pedido));
        if (pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(LocalDateTime.now());
        }
        Pedido guardado = pedidoService.guardar(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirPedido(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Long id,
            @RequestBody Pedido pedido) {
        try {
            Pedido existente = pedidoService.findById(id);
            if (existente == null) {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

        pedido.setId_Pedido(id);
        pedido.setCliente(buscarCliente(pedido));
        pedido.setOperador(buscarOperador(pedido));
        pedido.setDomiciliario(buscarDomiciliario(pedido));
        if (pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(LocalDateTime.now());
        }
        Pedido actualizado = pedidoService.guardar(pedido);
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
        if (pedido.getCliente() == null || pedido.getCliente().getClienteId() == null) {
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
        if (pedido.getOperador() == null || pedido.getOperador().getId_operador() == null) {
            return null;
        }
        return operadorRepository.findById(pedido.getOperador().getId_operador())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El operador seleccionado no existe"));
    }

    private Domiciliario buscarDomiciliario(Pedido pedido) {
        if (pedido.getDomiciliario() == null || pedido.getDomiciliario().getId_domiciliario() == null) {
            return null;
        }
        return domiciliarioRepository.findById(pedido.getDomiciliario().getId_domiciliario())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El domiciliario seleccionado no existe"));
    }

    private Map<String, Object> convertirPedido(Pedido pedido) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id_Pedido", pedido.getId_Pedido());
        map.put("estado", pedido.getEstado());
        map.put("fechaCreacion", pedido.getFechaCreacion());
        map.put("fechaEntrega", pedido.getFechaEntrega());

        if (pedido.getCliente() != null) {
            Map<String, Object> cliMap = new LinkedHashMap<>();
            cliMap.put("clienteId", pedido.getCliente().getClienteId());
            cliMap.put("nombre", pedido.getCliente().getNombre());
            cliMap.put("apellido", pedido.getCliente().getApellido());
            cliMap.put("correo", pedido.getCliente().getCorreo() != null ? pedido.getCliente().getCorreo() : "");
            cliMap.put("telefono", pedido.getCliente().getTelefono() != null ? pedido.getCliente().getTelefono() : "");
            cliMap.put("direccion", pedido.getCliente().getDireccion() != null ? pedido.getCliente().getDireccion() : "");
            cliMap.put("activo", pedido.getCliente().isActivo());
            map.put("cliente", cliMap);
        } else {
            map.put("cliente", null);
        }

        if (pedido.getOperador() != null) {
            map.put("operador", Map.of(
                    "id_operador", pedido.getOperador().getId_operador(),
                    "nombre", pedido.getOperador().getNombre()
            ));
        } else {
            map.put("operador", null);
        }

        if (pedido.getDomiciliario() != null) {
            Map<String, Object> domMap = new LinkedHashMap<>();
            domMap.put("id_domiciliario", pedido.getDomiciliario().getId_domiciliario());
            domMap.put("nombre", pedido.getDomiciliario().getNombre());
            domMap.put("celular", pedido.getDomiciliario().getCelular() != null ? pedido.getDomiciliario().getCelular() : "");
            domMap.put("cedula", pedido.getDomiciliario().getCedula() != null ? pedido.getDomiciliario().getCedula() : "");
            domMap.put("informaciondisp", pedido.getDomiciliario().isInformaciondisp());
            map.put("domiciliario", domMap);
        } else {
            map.put("domiciliario", null);
        }

        return map;
    }
}
