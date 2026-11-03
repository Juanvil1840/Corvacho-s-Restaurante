package com.example.demo.service;

import java.util.Collection;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.entities.Operador;

// Interfaz de servicio para la gestion y autenticacion de operadores
public interface OperadorService {
    // Lista todos los operadores registrados
    Collection<Operador> findAll();
    // Busca un operador por su id
    Operador findById(Long id);
    // Guarda directamente un operador
    Operador guardar(Operador operador);
    // Crea un nuevo operador validando sus datos
    Operador crearOperador(Operador operador);
    // Actualiza los datos de un operador existente
    Operador actualizarOperador(Long id, Operador datos);
    // Elimina un operador desvinculando sus relaciones
    boolean eliminarOperador(Long id);
    // Elimina un operador directamente por su id
    void deleteById(Long id);
    // Autentica un operador con usuario y contrasena
    Operador autenticar(String usuario, String contrasena);
    // Valida las credenciales de login y devuelve el operador autenticado
    Operador autenticarOperador(LoginRequestDTO credenciales);
}
