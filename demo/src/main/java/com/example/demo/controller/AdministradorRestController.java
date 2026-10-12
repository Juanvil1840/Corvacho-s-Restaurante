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

import com.example.demo.entities.Administrador;
import com.example.demo.service.AdministradorService;

@RestController
@RequestMapping("/api/administradores")
@CrossOrigin(origins = "http://localhost:4200")
public class AdministradorRestController {

    private final AdministradorService administradorService;

    public AdministradorRestController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping
    public Collection<Map<String, Object>> listar() {
        return administradorService.findAll()
                .stream()
                .map(this::convertirAdministrador)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
        Administrador administrador = administradorService.findById(id);
        if (administrador == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirAdministrador(administrador));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Administrador administrador) {
        if (administrador.getNombre() == null || administrador.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del administrador es obligatorio");
        }
        administrador.setId_administrador(null);
        Administrador guardado = administradorService.guardar(administrador);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirAdministrador(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Long id,
            @RequestBody Administrador administrador) {
        Administrador existente = administradorService.findById(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        if (administrador.getNombre() == null || administrador.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del administrador es obligatorio");
        }
        existente.setNombre(administrador.getNombre());
        Administrador actualizado = administradorService.guardar(existente);
        return ResponseEntity.ok(convertirAdministrador(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (administradorService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        administradorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> convertirAdministrador(Administrador admin) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id_administrador", admin.getId_administrador());
        map.put("nombre", admin.getNombre());
        return map;
    }
}

