package com.example.demo.service;

import com.example.demo.entities.Cliente;

public interface LoginService {
    boolean validarCredenciales(String correo, String contraseña);

    /**
     * Valida las credenciales y retorna el Cliente si son correctas, o null si no lo son.
     * Encapsula la lógica de autenticación completa para que el controller no acceda al repositorio.
     */
    Cliente autenticar(String correo, String contraseña);
}
