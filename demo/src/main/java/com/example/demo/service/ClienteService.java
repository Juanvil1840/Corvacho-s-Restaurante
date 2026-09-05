package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Cliente;

public interface ClienteService {

    // === MÉTODOS DE LECTURA ===
    Collection<Cliente> obtenerTodos();
    Cliente obtenerPorId(Integer id);
    boolean existe(Integer id);

    // === CRUD BÁSICO ===
    Cliente guardar(Cliente cliente);
    void eliminar(Integer id);

    // === LÓGICA DE NEGOCIO (antes estaba en el controller) ===
    Cliente registrar(Cliente cliente);      // guarda un cliente nuevo y lo activa
    Cliente actualizar(Cliente cliente);     // conserva la contraseña anterior si no se envía una nueva
    void desactivar(Integer id);
    void reactivar(Integer id);
}