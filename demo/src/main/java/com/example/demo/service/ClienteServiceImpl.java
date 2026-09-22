package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Cliente;
import com.example.demo.errors.ClienteNotFoundException;
import com.example.demo.repository.ClienteRepository;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Collection<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente registrar(Cliente cliente) {
        cliente.setActivo(true);
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente actualizar(Cliente cliente) {
        Cliente existente = clienteRepository.findById(cliente.getClienteId()).orElse(null);

        if (cliente.getContraseña() == null || cliente.getContraseña().isEmpty()) {
            if (existente != null) {
                cliente.setContraseña(existente.getContraseña());
            }
        }

        if (existente != null) {
            if (cliente.getCarrito() == null) {
                cliente.setCarrito(existente.getCarrito());
            }
            if (cliente.getPedidos() == null) {
                cliente.setPedidos(existente.getPedidos());
            }
            cliente.setActivo(existente.isActivo());
        }

        return clienteRepository.save(cliente);
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
}