package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.Cliente;
import com.example.demo.service.LoginService;

// Controlador para el inicio de sesion de clientes 
@Controller
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    // Muestra el formulario de inicio de sesion /login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    //  Si los datos que puso el cliente son correctos, lo redirige a su perfil, con /perfil/ver/id, donde e id es el que se le asigno al crear la cuenta y si no, error
    @PostMapping("/login")
    public String procesarLogin( @RequestParam String correo, @RequestParam String contraseña,Model model) {

        Cliente cliente = loginService.autenticar(correo, contraseña);

        if (cliente != null) {
            return "redirect:/perfil/ver/" + cliente.getClienteId();
        }

        model.addAttribute("error", true);
        return "login";
    }
}