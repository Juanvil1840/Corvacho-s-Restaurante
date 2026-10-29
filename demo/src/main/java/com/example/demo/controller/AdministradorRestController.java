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

import com.example.demo.dto.AdministradorDTO;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.MensajeDTO;
import com.example.demo.entities.Administrador;
import com.example.demo.service.AdministradorService;

// Controlador REST Administradores y autenticacion
@RestController
@RequestMapping("/api/administradores")
@CrossOrigin(origins = "http://localhost:4200")
public class AdministradorRestController {

    private final AdministradorService administradorService;

    // Constructor para inyectar el servicio de administradores
    public AdministradorRestController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    // Lista todos los administradores registrados en el sistema
    @GetMapping
    public Collection<AdministradorDTO> listar() {
        return administradorService.findAll()
                .stream()
                .map(AdministradorDTO::fromEntity)
                .toList();
    }

    // Busca un administrador por su identificador unico
    @GetMapping("/{id}")
    public ResponseEntity<AdministradorDTO> buscarPorId(@PathVariable Long id) {
        Administrador administrador = administradorService.findById(id);
        if (administrador == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AdministradorDTO.fromEntity(administrador));
    }

    // Crea un nuevo administrador en la base de datos
    @PostMapping
    public ResponseEntity<AdministradorDTO> crear(@RequestBody Administrador administrador) {
        Administrador guardado = administradorService.crearAdministrador(administrador);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdministradorDTO.fromEntity(guardado));
    }

    // Actualiza los datos de un administrador existente
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

    // Elimina un administrador del sistema
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (administradorService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        administradorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para autenticar a un administrador mediante usuario y contrasena
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO credenciales) {
        String usuario = credenciales.getUsuario();
        String contrasena = credenciales.getContrasena();

        // Validacion de que los campos no lleguen vacios al hacer el login
        if (usuario == null || contrasena == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MensajeDTO("Usuario y contrasena son obligatorios."));
        }

        // Se delega la validacion al servicio
        Administrador admin = administradorService.autenticar(usuario.trim(), contrasena);

        // Si las credenciales son incorrectas se devuelve un mensaje de usuario o contrasena incorrectos
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MensajeDTO("Usuario o contrasena incorrectos"));
        }

        // Si el login es exitoso, se devuelve el DTO con los datos del admin
        return ResponseEntity.ok(AdministradorDTO.fromEntity(admin));
    }
}