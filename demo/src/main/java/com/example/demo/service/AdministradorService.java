package com.example.demo.service;

import java.util.Collection;

import com.example.demo.entities.Administrador;

// Interfaz de servicio para la gestion y autenticacion de administradores
public interface AdministradorService {
    // Lista todos los administradores
    Collection<Administrador> findAll();
    // Busca un administrador por su id
    Administrador findById(Long id);
    // Guarda un administrador directamente
    Administrador guardar(Administrador administrador);
    // Crea un nuevo administrador validando sus datos
    Administrador crearAdministrador(Administrador administrador);
    // Actualiza un administrador existente
    Administrador actualizarAdministrador(Long id, Administrador administrador);
    // Elimina un administrador por su id
    void deleteById(Long id);
    // Autentica a un administrador con su usuario y contrasena
    Administrador autenticar(String usuario, String contrasena);
}
