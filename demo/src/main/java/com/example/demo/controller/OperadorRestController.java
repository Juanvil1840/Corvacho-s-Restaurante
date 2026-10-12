package com.example.demo.controller;

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

import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Operador;
import com.example.demo.entities.Pedido;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.service.OperadorService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/operadores")
@CrossOrigin(origins = "http://localhost:4200")
public class OperadorRestController {

    private final OperadorService operadorService;
    private final DomiciliarioRepository domiciliarioRepository;
    private final PedidoRepository pedidoRepository;

    public OperadorRestController(
            OperadorService operadorService,
            DomiciliarioRepository domiciliarioRepository,
            PedidoRepository pedidoRepository) {
        this.operadorService = operadorService;
        this.domiciliarioRepository = domiciliarioRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public Collection<Map<String, Object>> listar() {
        return operadorService.findAll()
                .stream()
                .map(this::convertirOperador)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
        Operador operador = operadorService.findById(id);
        if (operador == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirOperador(operador));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Operador operador) {
        validarOperador(operador, null);
        operador.setId_operador(null);
        Operador guardado = operadorService.guardar(operador);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirOperador(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Long id,
            @RequestBody Operador datos) {
        Operador existente = operadorService.findById(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        validarOperador(datos, id);
        existente.setNombre(datos.getNombre());
        existente.setUsuario(datos.getUsuario());
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            existente.setContrasena(datos.getContrasena());
        }
        Operador actualizado = operadorService.guardar(existente);
        return ResponseEntity.ok(convertirOperador(actualizado));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Operador existente = operadorService.findById(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        if (existente.getPedidos() != null) {
            for (Pedido pedido : existente.getPedidos()) {
                pedido.setOperador(null);
                pedidoRepository.save(pedido);
            }
            existente.getPedidos().clear();
        }

        if (existente.getDomiciliarios() != null) {
            for (Domiciliario domiciliario : existente.getDomiciliarios()) {
                domiciliario.setOperador(null);
                domiciliarioRepository.save(domiciliario);
            }
            existente.getDomiciliarios().clear();
        }

        operadorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void validarOperador(Operador operador, Long idActual) {
        if (operador.getNombre() == null || operador.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del operador es obligatorio");
        }
        if (operador.getUsuario() == null || operador.getUsuario().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario del operador es obligatorio");
        }
        if (idActual == null && (operador.getContrasena() == null || operador.getContrasena().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña del operador es obligatoria");
        }

        boolean usuarioDuplicado = operadorService.findAll().stream()
                .anyMatch(op -> op.getUsuario().equalsIgnoreCase(operador.getUsuario().trim())
                        && (idActual == null || !op.getId_operador().equals(idActual)));
        if (usuarioDuplicado) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está registrado");
        }
    }

    private Map<String, Object> convertirOperador(Operador op) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id_operador", op.getId_operador());
        map.put("nombre", op.getNombre());
        map.put("usuario", op.getUsuario());
        map.put("contrasena", op.getContrasena());
        return map;
    }
}

