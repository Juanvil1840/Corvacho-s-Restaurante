package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.entities.Cliente;
import com.example.demo.repository.ClienteRepository;

@Service
public class LoginServiceImpl implements LoginService {

    private final ClienteRepository clienteRepository;

    public LoginServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente autenticar(String correo, String contrasena) {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente != null && cliente.getContrasena().equals(contrasena)) {
            return cliente;
        }
        return null;
    }
}
