package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Override
    public boolean validarCredenciales(String correo, String contraseña) {
        return "admin".equals(correo) && "admin".equals(contraseña);
    }
}
