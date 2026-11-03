package com.example.demo.service;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.LoginClienteDTO;
import com.example.demo.entities.Cliente;
import com.example.demo.repository.ClienteRepository;

// Implementacion del servicio para la gestion de clientes
@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final LoginService loginService;

    // Constructor para inyectar el repositorio de clientes y el servicio de login
    public ClienteServiceImpl(ClienteRepository clienteRepository, LoginService loginService) {
        this.clienteRepository = clienteRepository;
        this.loginService = loginService;
    }

    // Obtiene todos los clientes registrados
    @Override
    public Collection<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    // Busca un cliente por su id o retorna null si no existe
    @Override
    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElse(null);
    }

    // Guarda un cliente en la base de datos
    @Override
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Crea un nuevo cliente validando que el correo no este repetido y asignando valores por defecto si faltan
    @Override
    public Cliente crearCliente(Cliente cliente) {
        validarCliente(cliente, null);
        // Verifica que el correo electronico no este registrado previamente
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

    // Actualiza los datos de un cliente existente validando que el correo no pertenezca a otro cliente
    @Override
    public Cliente actualizarCliente(Long id, Cliente datos) {
        if (!existe(id)) {
            return null;
        }
        validarCliente(datos, id);
        Cliente existente = obtenerPorId(id);

        // Valida que el nuevo correo no este en uso por otro cliente distinto
        Cliente correoOtro = clienteRepository.findByCorreo(datos.getCorreo());
        if (correoOtro != null && !correoOtro.getClienteId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este correo ya está registrado por otro cliente");
        }

        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setCorreo(datos.getCorreo());
        // Solo actualiza la contrasena si se proporciona una nueva
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            existente.setContrasena(datos.getContrasena());
        }
        existente.setTelefono(datos.getTelefono() != null && !datos.getTelefono().isBlank() ? datos.getTelefono() : "Sin teléfono");
        existente.setDireccion(datos.getDireccion() != null && !datos.getDireccion().isBlank() ? datos.getDireccion() : "Sin dirección");
        existente.setActivo(datos.isActivo());

        return clienteRepository.save(existente);
    }

    // Marca la cuenta de un cliente como inactiva
    @Override
    public void desactivar(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente != null) {
            cliente.setActivo(false);
            clienteRepository.save(cliente);
        }
    }

    // Marca la cuenta de un cliente como activa nuevamente
    @Override
    public void reactivar(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente != null) {
            cliente.setActivo(true);
            clienteRepository.save(cliente);
        }
    }

    // Elimina un cliente de la base de datos por su id
    @Override
    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }

    // Verifica si existe un cliente con el id especificado
    @Override
    public boolean existe(Long id) {
        return clienteRepository.existsById(id);
    }

    // Autentica a un cliente verificando sus credenciales y que su cuenta este activa
    @Override
    public Cliente autenticarCliente(LoginClienteDTO credenciales) {
        if (credenciales == null || credenciales.getCorreo() == null || credenciales.getContrasena() == null) {
            return null;
        }
        Cliente cliente = loginService.autenticar(credenciales.getCorreo().trim(), credenciales.getContrasena());
        if (cliente == null) {
            return null;
        }
        // Si el cliente tiene la cuenta desactivada no se le permite iniciar sesion
        if (!cliente.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tu cuenta está desactivada.");
        }
        return cliente;
    }

    // Valida que los datos obligatorios del cliente no esten vacios
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
