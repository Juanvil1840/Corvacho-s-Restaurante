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

import com.example.demo.dto.LoginOperadorDTO;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.OperadorDTO;
import com.example.demo.entities.Operador;
import com.example.demo.service.OperadorService;

@RestController
@RequestMapping("/api/operadores")
@CrossOrigin(origins = "http://localhost:4200")
public class OperadorRestController {

    private final OperadorService operadorService;

    public OperadorRestController(OperadorService operadorService) {
        this.operadorService = operadorService;
    }

    @GetMapping
    public Collection<OperadorDTO> listar() {
        return operadorService.findAll()
                .stream()
                .map(OperadorDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperadorDTO> buscarPorId(@PathVariable Long id) {
        Operador operador = operadorService.findById(id);
        if (operador == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(OperadorDTO.fromEntity(operador));
    }

    @PostMapping
    public ResponseEntity<OperadorDTO> crear(@RequestBody Operador operador) {
        Operador guardado = operadorService.crearOperador(operador);
        return ResponseEntity.status(HttpStatus.CREATED).body(OperadorDTO.fromEntity(guardado));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!operadorService.eliminarOperador(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginOperadorDTO> login(@RequestBody LoginRequestDTO credenciales) {
        Operador operador = operadorService.autenticarOperador(credenciales);
        return ResponseEntity.ok(new LoginOperadorDTO(
                operador.getId_operador(),
                operador.getNombre(),
                operador.getUsuario()
        ));
    }
}
