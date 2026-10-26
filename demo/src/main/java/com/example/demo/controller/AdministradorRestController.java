package com.example.demo.controller;

import java.util.Collection;
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

import com.example.demo.dto.AdministradorDTO;
import com.example.demo.dto.MensajeDTO;
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
    public Collection<AdministradorDTO> listar() {
        return administradorService.findAll()
                .stream()
                .map(AdministradorDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministradorDTO> buscarPorId(@PathVariable Long id) {
        Administrador administrador = administradorService.findById(id);
        if (administrador == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AdministradorDTO.fromEntity(administrador));
    }

    @PostMapping
    public ResponseEntity<AdministradorDTO> crear(@RequestBody Administrador administrador) {
        Administrador guardado = administradorService.crearAdministrador(administrador);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdministradorDTO.fromEntity(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministradorDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Administrador administrador) {
        Administrador actualizado = administradorService.actualizarAdministrador(id, administrador);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AdministradorDTO.fromEntity(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (administradorService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        administradorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
        @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String usuario = credenciales.get("usuario");
        String contrasena = credenciales.get("contrasena");

        if (usuario == null || contrasena == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MensajeDTO("Usuario y contraseña son obligatorios."));
        }

        Administrador admin = administradorService.autenticar(usuario.trim(), contrasena);

        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MensajeDTO("Usuario o contraseña incorrectos"));
        }

        return ResponseEntity.ok(AdministradorDTO.fromEntity(admin));
    }
}
