package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.ProductoService;

@Controller
public class MenuController {

@Autowired
    private ProductoService productoService;

    @GetMapping("/menu")
    public String menu(Model model){
        model.addAttribute("productos", productoService.obtenerTodos());
        return "menu";
    }


}
