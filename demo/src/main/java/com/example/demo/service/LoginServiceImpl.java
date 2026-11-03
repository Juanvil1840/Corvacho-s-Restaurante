package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.entities.Cliente;
import com.example.demo.repository.ClienteRepository;

// Implementacion del servicio de login para clientes
@Service
public class LoginServiceImpl implements LoginService {

    private final ClienteRepository clienteRepository;

    // Constructor para inyectar el repositorio de clientes
    public LoginServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Busca al cliente por correo y comprueba si la contrasena coincide
    @Override
    public Cliente autenticar(String correo, String contrasena) {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente != null && cliente.getContrasena().equals(contrasena)) {
            return cliente;
        }
        return null;
    }
}
