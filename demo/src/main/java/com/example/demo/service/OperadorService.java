package com.example.demo.service;

import java.util.Collection;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.entities.Operador;

public interface OperadorService {
    Collection<Operador> findAll();
    Operador findById(Long id);
    Operador guardar(Operador operador);
    Operador crearOperador(Operador operador);
    Operador actualizarOperador(Long id, Operador datos);
    boolean eliminarOperador(Long id);
    void deleteById(Long id);
    Operador autenticar(String usuario, String contrasena);
    Operador autenticarOperador(LoginRequestDTO credenciales);
}
