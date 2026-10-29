package com.example.demo.service;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.LoginClienteDTO;
import com.example.demo.entities.Cliente;
import com.example.demo.repository.ClienteRepository;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final LoginService loginService;

    public ClienteServiceImpl(ClienteRepository clienteRepository, LoginService loginService) {
        this.clienteRepository = clienteRepository;
        this.loginService = loginService;
    }

    @Override
    public Collection<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElse(null);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente crearCliente(Cliente cliente) {
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
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente actualizarCliente(Long id, Cliente datos) {
        if (!existe(id)) {
            return null;
        }
        validarCliente(datos, id);
        Cliente existente = obtenerPorId(id);

        Cliente correoOtro = clienteRepository.findByCorreo(datos.getCorreo());
        if (correoOtro != null && !correoOtro.getClienteId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este correo ya está registrado por otro cliente");
        }

        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setCorreo(datos.getCorreo());
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            existente.setContrasena(datos.getContrasena());
        }
        existente.setTelefono(datos.getTelefono() != null && !datos.getTelefono().isBlank() ? datos.getTelefono() : "Sin teléfono");
        existente.setDireccion(datos.getDireccion() != null && !datos.getDireccion().isBlank() ? datos.getDireccion() : "Sin dirección");
        existente.setActivo(datos.isActivo());

        return clienteRepository.save(existente);
    }

    @Override
    public void desactivar(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente != null) {
            cliente.setActivo(false);
            clienteRepository.save(cliente);
        }
    }

    @Override
    public void reactivar(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente != null) {
            cliente.setActivo(true);
            clienteRepository.save(cliente);
        }
    }

    @Override
    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public boolean existe(Long id) {
        return clienteRepository.existsById(id);
    }

    @Override
    public Cliente autenticarCliente(LoginClienteDTO credenciales) {
        if (credenciales == null || credenciales.getCorreo() == null || credenciales.getContrasena() == null) {
            return null;
        }
        Cliente cliente = loginService.autenticar(credenciales.getCorreo().trim(), credenciales.getContrasena());
        if (cliente == null) {
            return null;
        }
        if (!cliente.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta está desactivada.");
        }
        return cliente;
    }

    private void validarCliente(Cliente cliente, Long idActual) {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank() ||
            cliente.getApellido() == null || cliente.getApellido().isBlank() ||
            cliente.getCorreo() == null || cliente.getCorreo().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre, apellido y correo son obligatorios");
        }
        if (idActual == null && (cliente.getContrasena() == null || cliente.getContrasena().isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre, apellido, correo y contraseña son obligatorios");
        }
    }
}
