package com.example.demo.controller;

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

import com.example.demo.dto.DomiciliarioDTO;
import com.example.demo.entities.Domiciliario;
import com.example.demo.service.DomiciliarioService;

// Controlador REST para la gestion de Domiciliarios
@RestController
@RequestMapping("/api/domiciliarios")
@CrossOrigin(origins = "http://localhost:4200")
public class DomiciliarioRestController {

    private final DomiciliarioService domiciliarioService;

    // Constructor para inyectar el servicio de domiciliarios
    public DomiciliarioRestController(DomiciliarioService domiciliarioService) {
        this.domiciliarioService = domiciliarioService;
    }

    // Lista todos los domiciliarios registrados
    @GetMapping
    public Collection<DomiciliarioDTO> listar() {
        return domiciliarioService.findAll()
                .stream()
                .map(DomiciliarioDTO::fromEntity)
                .toList();
    }

    // Lista unicamente los domiciliarios disponibles para asignar a un pedido
    @GetMapping("/disponibles")
    public Collection<DomiciliarioDTO> listarDisponibles() {
        return domiciliarioService.obtenerDisponibles()
                .stream()
                .map(DomiciliarioDTO::fromEntity)
                .toList();
    }

    // Busca un domiciliario por su id
    @GetMapping("/{id}")
    public ResponseEntity<DomiciliarioDTO> buscarPorId(@PathVariable Long id) {
        Domiciliario domiciliario = domiciliarioService.findById(id);
        if (domiciliario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(DomiciliarioDTO.fromEntity(domiciliario));
    }

    // Crea un nuevo domiciliario en la base de datos
    @PostMapping
    public ResponseEntity<DomiciliarioDTO> crear(@RequestBody Domiciliario domiciliario) {
        Domiciliario guardado = domiciliarioService.crearDomiciliario(domiciliario);
        return ResponseEntity.status(HttpStatus.CREATED).body(DomiciliarioDTO.fromEntity(guardado));
    }

    // Actualiza los datos de un domiciliario existente
    @PutMapping("/{id}")
    public ResponseEntity<DomiciliarioDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Domiciliario domiciliario) {
        Domiciliario actualizado = domiciliarioService.actualizarDomiciliario(id, domiciliario);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(DomiciliarioDTO.fromEntity(actualizado));
    }

    // Elimina un domiciliario de la base de datos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (domiciliarioService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        domiciliarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}