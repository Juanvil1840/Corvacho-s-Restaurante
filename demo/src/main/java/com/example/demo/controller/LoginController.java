package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.Cliente;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.service.LoginService;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String contraseña,
                                Model model) {
        if (loginService.validarCredenciales(correo, contraseña)) {
            Cliente cliente = clienteRepository.findByCorreo(correo);
            return "redirect:/perfil/ver/" + cliente.getClienteId();
        }
        model.addAttribute("error", true);
        return "login";
    }
}
