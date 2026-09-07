package com.example.demo.service;

import java.util.Collection;
import com.example.demo.entities.Cliente;

public interface ClienteService { 
    
    Collection<Cliente> obtenerTodos(); //Obtenemos todos los clientes que existan
    Cliente obtenerPorId(Long id); //Obtenemos a un solo cliente por su id 
    Cliente registrar(Cliente cliente);
    Cliente actualizar(Cliente cliente);
    void desactivar(Long id);
    void reactivar(Long id);
    Cliente guardar(Cliente cliente); //Guardamos un objeto cliente o tambien se puede actualizar
    void eliminar(Long id); //Eliminar un cliente por su id
    boolean existe(Long id); //Verificar si existe algun cliente con ese ID
}