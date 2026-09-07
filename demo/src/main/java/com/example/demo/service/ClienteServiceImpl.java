package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.errors.ClienteNotFoundException; 

import com.example.demo.entities.Cliente;
import com.example.demo.repository.ClienteRepository;

@Service // Servicio
public class ClienteServiceImpl implements ClienteService {

    @Autowired // Inyeccion de dependencias
    private ClienteRepository clienteRepository; // Variable clienteRepository de ClienteRepository para usarla en
                                                 // service

    @Override
    public Collection<Cliente> obtenerTodos() { // Funcion para obtener a todos los clientes
        return clienteRepository.findAll(); // En el clienteRepository se hace un findAll para encontrarlos a todos
    }

   @Override
public Cliente obtenerPorId(Long id) {
    Cliente cliente = clienteRepository.findById(id);
    if (cliente == null) {
        throw new ClienteNotFoundException(id); 
    }
    return cliente;
}

    @Override
    public Cliente guardar(Cliente cliente) { // Funcion para guardar un cliente
        return clienteRepository.save(cliente); // Guardamos al cliente o actualizamos su informacion
    }

    @Override
    public Cliente registrar(Cliente cliente) { // Registra un cliente nuevo y lo activa
        cliente.setActivo(true); // Como el cliente ya se registro, activo=true para que pueda iniciar sesion
        return clienteRepository.save(cliente); // Guardamos al nuevo cliente
    }

    @Override
    public Cliente actualizar(Cliente cliente) { // Actualiza datos del cliente
        // Si la contraseña viene vacia o nula, preservamos la contraseña original
        if (cliente.getContraseña() == null || cliente.getContraseña().isEmpty()) {
            Cliente existente = clienteRepository.findById(cliente.getClienteId());
            if (existente != null) {
                cliente.setContraseña(existente.getContraseña()); // Mantenemos la contraseña anterior
            }
        }
        return clienteRepository.save(cliente); // Guardamos el cliente actualizado
    }

    @Override
    public void desactivar(Long id) { // Desactiva la cuenta del cliente
        Cliente cliente = clienteRepository.findById(id);
        if (cliente != null) {
            cliente.setActivo(false); // Activo pasa a false
            clienteRepository.save(cliente);
        }
    }

    @Override
    public void reactivar(Long id) { // Reactiva la cuenta del cliente
        Cliente cliente = clienteRepository.findById(id);
        if (cliente != null) {
            cliente.setActivo(true); // Activo vuelve a true
            clienteRepository.save(cliente);
        }
    }

    @Override
    public void eliminar(Long id) { // Funcion para eliminar a un cliente con su id
        clienteRepository.deleteById(id); // Eliminamos al cliente
    }

    @Override
    public boolean existe(Long id) { // Funcion para verificar que un cliente existe por su id
        return clienteRepository.existsById(id); // Retorna si existe con la funcion booleana de true o false
    }
}