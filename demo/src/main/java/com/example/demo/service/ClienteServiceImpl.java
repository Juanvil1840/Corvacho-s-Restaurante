package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Cliente;
import com.example.demo.repository.ClienteRepository;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired // Inyeccion de dependencias de Spring
    private ClienteRepository clienteRepository; // Utilizamos esta variable para hablar directamente con la base de datos

    @Override
    public Collection<Cliente> obtenerTodos() {
        return clienteRepository.findAll(); // Le pedimos al repositorio todos los clientes guardados
    }

    @Override
    public Cliente obtenerPorId(Integer id) {
        return clienteRepository.findById(id); // Le pedimos al repositorio el cliente con ese id especifico
    }

    @Override
    public boolean existe(Integer id) {
        return clienteRepository.existsById(id); // Le preguntamos al repositorio si ese id ya esta registrado
    }


    @Override
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente); // Guardamos el cliente sin aplicarle ninguna regla adicional
    }

    @Override
    public void eliminar(Integer id) {
        clienteRepository.deleteById(id); // Le pedimos al repositorio que borre el cliente con ese id
    }

    
    @Override
    public Cliente registrar(Cliente cliente) {
        cliente.setActivo(true); // Como el cliente se esta registrando por primera vez, activo=true, para que pueda iniciar sesion despues
        return clienteRepository.save(cliente); // Guardamos el cliente ya activado
    }

    @Override
    public Cliente actualizar(Cliente cliente) {
        // Si la contraseña del cliente esta vacia o es null buscamos su antigua contraseña y la conservamos
        if (cliente.getContraseña() == null || cliente.getContraseña().isEmpty()) {
            Cliente existente = clienteRepository.findById(cliente.getClienteId()); // Buscamos al cliente actual en el repositorio
            if (existente != null) {
                cliente.setContraseña(existente.getContraseña()); // Le asignamos la contraseña que ya tenia guardada
            }
        }
        return clienteRepository.save(cliente); // Guardamos el cliente actualizado con los cambios que haya realizado
    }

    @Override
    public void desactivar(Integer id) {
        Cliente cliente = clienteRepository.findById(id); // Buscamos el cliente por su id en el repositorio
        if (cliente != null) { // Si tenemos un cliente que no este vacio
            cliente.setActivo(false); // Su parametro de activo ahora pasa a falso, lo contrario a cuando se guarda por primera vez que esta en true
            clienteRepository.save(cliente); // Guardamos los cambios
        }
    }

    @Override
    public void reactivar(Integer id) {
        Cliente cliente = clienteRepository.findById(id); // Buscamos al cliente por id
        if (cliente != null) { // Si el cliente existe
            cliente.setActivo(true); // La propiedad del cliente de activo vuelve a pasar a true
            clienteRepository.save(cliente); // Guardamos los cambios
        }
    }
}