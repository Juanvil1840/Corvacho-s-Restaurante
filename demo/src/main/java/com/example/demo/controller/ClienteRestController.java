package com.example.demo.controller;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.example.demo.dto.ClienteDTO;
import com.example.demo.dto.LoginClienteDTO;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Role;
import com.example.demo.security.AuthService;
import com.example.demo.service.ClienteService;

// Controlador REST para la gestion de Clientes
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteRestController {

    private final ClienteService clienteService;
    private final AuthService authService;

    // Constructor para inyectar el servicio de clientes y el de autenticacion (JWT)
    public ClienteRestController(ClienteService clienteService, AuthService authService) {
        this.clienteService = clienteService;
        this.authService = authService;
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
    @PreAuthorize("hasAuthority('ADMIN') or @seguridad.esElCliente(#id)")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.obtenerPorId(id);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ClienteDTO.fromEntity(cliente));
    }

    // Crea un nuevo cliente en la base de datos
    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@RequestBody Cliente cliente) {
        Cliente guardado = clienteService.crearCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteDTO.fromEntity(guardado));
    }

    // Actualiza los datos de un cliente existente
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @seguridad.esElCliente(#id)")
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
    @PreAuthorize("hasAuthority('ADMIN') or @seguridad.esElCliente(#id)")
    public ResponseEntity<ClienteDTO> desactivar(@PathVariable Long id) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.desactivar(id);
        return ResponseEntity.ok(ClienteDTO.fromEntity(clienteService.obtenerPorId(id)));
    }

    // Reactiva la cuenta de un cliente
    @PutMapping("/{id}/reactivar")
    @PreAuthorize("hasAuthority('ADMIN') or @seguridad.esElCliente(#id)")
    public ResponseEntity<ClienteDTO> reactivar(@PathVariable Long id) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.reactivar(id);
        return ResponseEntity.ok(ClienteDTO.fromEntity(clienteService.obtenerPorId(id)));
    }

    // Elimina un cliente de la base de datos
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @seguridad.esElCliente(#id)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para la autenticacion de clientes: devuelve el token JWT, el rol y los datos del cliente
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO<ClienteDTO>> login(@RequestBody LoginClienteDTO credenciales) {
        // Valida la contrasena y que la cuenta este activa (si esta desactivada responde 403)
        Cliente cliente = clienteService.autenticarCliente(credenciales);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authService.login(cliente.getCorreo(), credenciales.getContrasena(), Role.CLIENTE);
        return ResponseEntity.ok(new AuthResponseDTO<>(token, Role.CLIENTE, ClienteDTO.fromEntity(cliente)));
    }
}
