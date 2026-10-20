package com.example.demo.controller;

import java.util.Collection;

import com.example.demo.dto.LoginClienteDTO;

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

import com.example.demo.dto.ClienteDTO;
import com.example.demo.entities.Cliente;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.service.ClienteService;
import com.example.demo.service.LoginService;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteRestController {

    private final ClienteService clienteService;
    private final ClienteRepository clienteRepository;
    private final LoginService loginService;

    public ClienteRestController(
            ClienteService clienteService,
            ClienteRepository clienteRepository,
            LoginService loginService) {
        this.clienteService = clienteService;
        this.clienteRepository = clienteRepository;
        this.loginService = loginService;
    }

    @GetMapping
    public Collection<ClienteDTO> listar() {
        return clienteService.obtenerTodos()
                .stream()
                .map(ClienteDTO::fromEntity)
                .toList();
    }

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

    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@RequestBody Cliente cliente) {
        validarCliente(cliente, null);
        Cliente existenteCorreo = clienteRepository.findByCorreo(cliente.getCorreo());
        if (existenteCorreo != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este correo ya está registrado");
        }
        cliente.setClienteId(null);
        if (cliente.getTelefono() == null || cliente.getTelefono().isBlank()) {
            cliente.setTelefono("Sin teléfono");
        }
        if (cliente.getDireccion() == null || cliente.getDireccion().isBlank()) {
            cliente.setDireccion("Sin dirección");
        }
        Cliente guardado = clienteService.guardar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteDTO.fromEntity(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Cliente datos) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        validarCliente(datos, id);
        Cliente existente = clienteService.obtenerPorId(id);

        Cliente correoOtro = clienteRepository.findByCorreo(datos.getCorreo());
        if (correoOtro != null && !correoOtro.getClienteId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este correo ya está registrado por otro cliente");
        }

        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setCorreo(datos.getCorreo());
        if (datos.getContraseña() != null && !datos.getContraseña().isBlank()) {
            existente.setContraseña(datos.getContraseña());
        }
        existente.setTelefono(datos.getTelefono() != null && !datos.getTelefono().isBlank() ? datos.getTelefono() : "Sin teléfono");
        existente.setDireccion(datos.getDireccion() != null && !datos.getDireccion().isBlank() ? datos.getDireccion() : "Sin dirección");
        existente.setActivo(datos.isActivo());

        Cliente actualizado = clienteService.guardar(existente);
        return ResponseEntity.ok(ClienteDTO.fromEntity(actualizado));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ClienteDTO> desactivar(@PathVariable Long id) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.desactivar(id);
        return ResponseEntity.ok(ClienteDTO.fromEntity(clienteService.obtenerPorId(id)));
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<ClienteDTO> reactivar(@PathVariable Long id) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.reactivar(id);
        return ResponseEntity.ok(ClienteDTO.fromEntity(clienteService.obtenerPorId(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<ClienteDTO> login(@RequestBody LoginClienteDTO credenciales) {
        if (credenciales == null || credenciales.getCorreo() == null || credenciales.getContrasena() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String correo = credenciales.getCorreo();
        String contrasena = credenciales.getContrasena();
        Cliente cliente = loginService.autenticar(correo.trim(), contrasena);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!cliente.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta está desactivada.");
        }
        return ResponseEntity.ok(ClienteDTO.fromEntity(cliente));
    }

    private void validarCliente(Cliente cliente, Long idActual) {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank() ||
            cliente.getApellido() == null || cliente.getApellido().isBlank() ||
            cliente.getCorreo() == null || cliente.getCorreo().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre, apellido y correo son obligatorios");
        }
        if (idActual == null && (cliente.getContraseña() == null || cliente.getContraseña().isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre, apellido, correo y contraseña son obligatorios");
        }
    }
}

