package com.example.demo.service;

import com.example.demo.entities.Cliente;

public interface LoginService {

    // Devuelve el cliente si el correo y la contrasena son correctos, si no devuelve null
    Cliente autenticar(String correo, String contrasena);
}
