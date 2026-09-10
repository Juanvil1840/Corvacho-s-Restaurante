package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Cliente;
import com.example.demo.repository.ClienteRepository;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public boolean validarCredenciales(String correo, String contraseña) {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente != null && cliente.getContraseña().equals(contraseña)) {
            return true;
        }
        return false;
    }

    @Override
    public Cliente autenticar(String correo, String contraseña) {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente != null && cliente.getContraseña().equals(contraseña)) {
            return cliente;
        }
        return null;
    }
}
