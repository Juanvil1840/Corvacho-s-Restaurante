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
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.DomiciliarioDTO;
import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Operador;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.service.DomiciliarioService;

@RestController
@RequestMapping("/api/domiciliarios")
@CrossOrigin(origins = "http://localhost:4200")
public class DomiciliarioRestController {

    private final DomiciliarioService domiciliarioService;
    private final OperadorRepository operadorRepository;

    public DomiciliarioRestController(
            DomiciliarioService domiciliarioService,
            OperadorRepository operadorRepository) {
        this.domiciliarioService = domiciliarioService;
        this.operadorRepository = operadorRepository;
    }

    @GetMapping
    public Collection<DomiciliarioDTO> listar() {
        return domiciliarioService.findAll()
                .stream()
                .map(DomiciliarioDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DomiciliarioDTO> buscarPorId(@PathVariable Long id) {
        Domiciliario domiciliario = domiciliarioService.findById(id);
        if (domiciliario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(DomiciliarioDTO.fromEntity(domiciliario));
    }

    @PostMapping
    public ResponseEntity<DomiciliarioDTO> crear(@RequestBody Domiciliario domiciliario) {
        domiciliario.setId_domiciliario(null);
        domiciliario.setOperador(buscarOperador(domiciliario));
        Domiciliario guardado = domiciliarioService.guardar(domiciliario);
        return ResponseEntity.status(HttpStatus.CREATED).body(DomiciliarioDTO.fromEntity(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DomiciliarioDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Domiciliario domiciliario) {
        if (domiciliarioService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        domiciliario.setId_domiciliario(id);
        domiciliario.setOperador(buscarOperador(domiciliario));
        Domiciliario actualizado = domiciliarioService.guardar(domiciliario);
        return ResponseEntity.ok(DomiciliarioDTO.fromEntity(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (domiciliarioService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        domiciliarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Operador buscarOperador(Domiciliario domiciliario) {
        if (domiciliario.getOperador() == null || domiciliario.getOperador().getId_operador() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un operador");
        }
        return operadorRepository.findById(domiciliario.getOperador().getId_operador())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El operador seleccionado no existe"));
    }
}
