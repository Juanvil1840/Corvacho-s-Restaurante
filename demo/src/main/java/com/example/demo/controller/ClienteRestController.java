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
    public Collection<Map<String, Object>> listar() {
        return clienteService.obtenerTodos()
                .stream()
                .map(this::convertirCliente)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.obtenerPorId(id);
            if (cliente == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(convertirCliente(cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Map<String, Object> body) {
        Cliente cliente = mapearCliente(body, null);
        Cliente existenteCorreo = clienteRepository.findByCorreo(cliente.getCorreo());
        if (existenteCorreo != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este correo ya está registrado");
        }
        Cliente guardado = clienteService.guardar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirCliente(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        Cliente existente = clienteService.obtenerPorId(id);
        Cliente datos = mapearCliente(body, existente);

        Cliente correoOtro = clienteRepository.findByCorreo(datos.getCorreo());
        if (correoOtro != null && !correoOtro.getClienteId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este correo ya está registrado por otro cliente");
        }

        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setCorreo(datos.getCorreo());
        existente.setContraseña(datos.getContraseña());
        existente.setTelefono(datos.getTelefono());
        existente.setDireccion(datos.getDireccion());
        existente.setActivo(datos.isActivo());

        Cliente actualizado = clienteService.guardar(existente);
        return ResponseEntity.ok(convertirCliente(actualizado));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Map<String, Object>> desactivar(@PathVariable Long id) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.desactivar(id);
        return ResponseEntity.ok(convertirCliente(clienteService.obtenerPorId(id)));
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Map<String, Object>> reactivar(@PathVariable Long id) {
        if (!clienteService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteService.reactivar(id);
        return ResponseEntity.ok(convertirCliente(clienteService.obtenerPorId(id)));
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
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String contrasena = credenciales.getOrDefault("contrasena", credenciales.get("contraseña"));
        if (correo == null || contrasena == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Cliente cliente = loginService.autenticar(correo.trim(), contrasena);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!cliente.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta está desactivada.");
        }
        return ResponseEntity.ok(convertirCliente(cliente));
    }

    private Cliente mapearCliente(Map<String, Object> body, Cliente existente) {
        String nombre = str(body.get("nombre"));
        String apellido = str(body.get("apellido"));
        String correo = str(body.get("correo"));
        String contrasena = str(body.getOrDefault("contrasena", body.get("contraseña")));
        String telefono = str(body.get("telefono"));
        String direccion = str(body.get("direccion"));
        Boolean activo = body.get("activo") instanceof Boolean b ? b : (existente != null ? existente.isActivo() : true);

        if (contrasena.isBlank() && existente != null) {
            contrasena = existente.getContraseña();
        }

        if (nombre.isBlank() || apellido.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre, apellido, correo y contraseña son obligatorios");
        }

        Cliente c = new Cliente();
        c.setNombre(nombre);
        c.setApellido(apellido);
        c.setCorreo(correo);
        c.setContraseña(contrasena);
        c.setTelefono(telefono.isBlank() ? "Sin teléfono" : telefono);
        c.setDireccion(direccion.isBlank() ? "Sin dirección" : direccion);
        c.setActivo(activo);
        return c;
    }

    private String str(Object val) {
        return val == null ? "" : val.toString().trim();
    }

    private Map<String, Object> convertirCliente(Cliente c) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("clienteId", c.getClienteId());
        map.put("nombre", c.getNombre());
        map.put("apellido", c.getApellido());
        map.put("correo", c.getCorreo());
        map.put("contrasena", c.getContraseña());
        map.put("telefono", c.getTelefono());
        map.put("direccion", c.getDireccion());
        map.put("activo", c.isActivo());
        return map;
    }
}

