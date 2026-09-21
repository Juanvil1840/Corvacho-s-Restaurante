package com.example.demo.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.entities.Producto;
import com.example.demo.service.CategoriaService;
import com.example.demo.service.ProductoService;

@Controller
public class MenuController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/menu")
    public String menu(Model model) {
        // Usamos Collection para coincidir con el tipo de retorno del servicio
        Collection<Producto> productos = productoService.obtenerTodos();
        
        Map<String, List<Producto>> productosPorCategoria = productos.stream()
                .collect(Collectors.groupingBy(p -> p.getCategoria().getNombre()));
        
        model.addAttribute("productosPorCategoria", productosPorCategoria);
        return "menu";
    }

    @GetMapping("/menu/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerPorId(id);

        if (producto == null) {
            return "redirect:/menu";
        }
        
        model.addAttribute("producto", producto);
        model.addAttribute("categoria", producto.getCategoria());
        return "detalle-producto";
    }

    @GetMapping("/menu/adminTable")
    public String getMethodName(Model model) {
        model.addAttribute("productos", productoService.obtenerTodos());
        return "adminMenu";
    }

}