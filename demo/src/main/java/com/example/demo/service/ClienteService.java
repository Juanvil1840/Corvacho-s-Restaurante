package com.example.demo.service;

import java.util.Collection;

import com.example.demo.dto.LoginClienteDTO;
import com.example.demo.entities.Cliente;

public interface ClienteService {

    // === MÉTODOS DE LECTURA ===
    Collection<Cliente> obtenerTodos();
    Cliente obtenerPorId(Long id);
    boolean existe(Long id);

    // === CRUD Y LÓGICA DE NEGOCIO ===
    Cliente guardar(Cliente cliente);
    Cliente crearCliente(Cliente cliente);
    Cliente actualizarCliente(Long id, Cliente datos);
    void eliminar(Long id);

    void desactivar(Long id);
    void reactivar(Long id);

    Cliente autenticarCliente(LoginClienteDTO credenciales);
}
