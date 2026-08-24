package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.demo.service.ProductoService;

@Controller
public class MenuController {

@Autowired
    private ProductoService productoService;

        public String menu(Model model){
        model.addAttribute("productos", productoService.obtenerTodos());
        return "menu";
    }


}
