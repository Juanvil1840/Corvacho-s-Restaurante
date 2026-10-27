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

import com.example.demo.dto.ClienteDTO;
import com.example.demo.dto.LoginClienteDTO;
import com.example.demo.entities.Cliente;
import com.example.demo.service.ClienteService;

// Controlador REST para la gestion de Clientes
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteRestController {

    private final ClienteService clienteService;

    public ClienteRestController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Lista todos los clientes registrados en la base de datos
    @GetMapping
    public Collection<ClienteDTO> listar() {
        return clienteService.obtenerTodos()
                .stream()
                .map(ClienteDTO::fromEntity)
                .toList();
    }

    // Busca un cliente por su identificador unico
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.obtenerPorId(id);
            if (cliente == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ClienteDTO.fromEntity(cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Crea un nuevo cliente en la base de datos
    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@RequestBody Cliente cliente) {
        Cliente guardado = clienteService.crearCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteDTO.fromEntity(guardado));
    }

    // Actualiza los datos de un cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Cliente datos) {
        Cliente actualizado = clienteService.actualizarCliente(id, datos);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ClienteDTO.fromEntity(actualizado));
    }

    // Desactiva la cuenta de un cliente
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ClienteDTO> desactivar(@PathVariable Long id) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.desactivar(id);
        return ResponseEntity.ok(ClienteDTO.fromEntity(clienteService.obtenerPorId(id)));
    }

    // Reactiva la cuenta de un cliente
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<ClienteDTO> reactivar(@PathVariable Long id) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.reactivar(id);
        return ResponseEntity.ok(ClienteDTO.fromEntity(clienteService.obtenerPorId(id)));
    }

    // Elimina un cliente de la base de datos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para la autenticacion de clientes
    @PostMapping("/login")
    public ResponseEntity<ClienteDTO> login(@RequestBody LoginClienteDTO credenciales) {
        Cliente cliente = clienteService.autenticarCliente(credenciales);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ClienteDTO.fromEntity(cliente));
    }
}