package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Cliente;

public interface ClienteService {
    // === MÉTODOS DE LECTURA ===
    Collection<Cliente> obtenerTodos();
    Cliente obtenerPorId(Integer id);

    // === MÉTODOS DE ESCRITURA (CRUD) ===
    Cliente guardar(Cliente cliente);
    void eliminar(Integer id);
    boolean existe(Integer id);
}