package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.entities.Cliente;
import com.example.demo.repository.ClienteRepository;

// Implementacion del servicio de login para clientes
@Service
public class LoginServiceImpl implements LoginService {

    private final ClienteRepository clienteRepository;
    private final UsuarioService usuarioService;

    // Constructor para inyectar el repositorio de clientes y el servicio de usuarios
    public LoginServiceImpl(ClienteRepository clienteRepository, UsuarioService usuarioService) {
        this.clienteRepository = clienteRepository;
        this.usuarioService = usuarioService;
    }

    // Busca al cliente por correo y comprueba si la contrasena coincide
    @Override
    public Cliente autenticar(String correo, String contrasena) {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente != null && usuarioService.coincide(contrasena, cliente.getContrasena())) {
            return cliente;
        }
        return null;
    }
}
