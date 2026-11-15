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

import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.OperadorDTO;
import com.example.demo.entities.Operador;
import com.example.demo.entities.Role;
import com.example.demo.security.AuthService;
import com.example.demo.service.OperadorService;

// Controlador REST para la gestion de Operadores
@RestController
@RequestMapping("/api/operadores")
@CrossOrigin(origins = "http://localhost:4200")
public class OperadorRestController {

    private final OperadorService operadorService;
    private final AuthService authService;

    // Constructor para inyectar el servicio de operadores y el de autenticacion (JWT)
    public OperadorRestController(OperadorService operadorService, AuthService authService) {
        this.operadorService = operadorService;
        this.authService = authService;
    }

    // Lista todos los operadores registrados en la base de datos
    @GetMapping
    public Collection<OperadorDTO> listar() {
        return operadorService.findAll()
                .stream()
                .map(OperadorDTO::fromEntity)
                .toList();
    }

    // Busca un operador por su id
    @GetMapping("/{id}")
    public ResponseEntity<OperadorDTO> buscarPorId(@PathVariable Long id) {
        Operador operador = operadorService.findById(id);
        if (operador == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(OperadorDTO.fromEntity(operador));
    }

    // Crea un nuevo operador en la base de datos
    @PostMapping
    public ResponseEntity<OperadorDTO> crear(@RequestBody Operador operador) {
        Operador guardado = operadorService.crearOperador(operador);
        return ResponseEntity.status(HttpStatus.CREATED).body(OperadorDTO.fromEntity(guardado));
    }

    // Actualiza los datos de un operador existente
    @PutMapping("/{id}")
    public ResponseEntity<OperadorDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Operador datos) {
        Operador actualizado = operadorService.actualizarOperador(id, datos);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(OperadorDTO.fromEntity(actualizado));
    }

    // Elimina un operador de la base de datos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!operadorService.eliminarOperador(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // Autentica a un operador: devuelve el token JWT, el rol y los datos del operador
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO<OperadorDTO>> login(@RequestBody LoginRequestDTO credenciales) {
        Operador operador = operadorService.autenticarOperador(credenciales);
        String token = authService.login(operador.getUsuario(), credenciales.getContrasena(), Role.OPERADOR);
        return ResponseEntity.ok(new AuthResponseDTO<>(token, Role.OPERADOR, OperadorDTO.fromEntity(operador)));
    }
}
