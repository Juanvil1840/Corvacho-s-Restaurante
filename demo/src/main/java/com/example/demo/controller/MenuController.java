package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.service.ProductoService;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.entities.Producto;
import com.example.demo.service.CategoriaService;

@Controller
public class MenuController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/menu")
    public String menu(Model model) {
        model.addAttribute("productos", productoService.obtenerTodos());
        return "menu";
    }

    @GetMapping("/menu/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {

        Producto producto = productoService.obtenerPorId(id);

        model.addAttribute("producto",producto);
        model.addAttribute("categoria", producto.getCategoria());
        return "detalle-producto";
    }

    @GetMapping("/menu/adminTable")
    public String getMethodName(Model model) {
        model.addAttribute("productos", productoService.obtenerTodos());
        return "adminMenu";
    }

}
