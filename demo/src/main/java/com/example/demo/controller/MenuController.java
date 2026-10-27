package com.example.demo.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.entities.Adicional;
import com.example.demo.entities.Producto;
import com.example.demo.service.AdicionalService;
import com.example.demo.service.CategoriaService;
import com.example.demo.service.ProductoService;

// Controlador para las vistas del menu de productos
@Controller
public class MenuController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final AdicionalService adicionalService;

    public MenuController( ProductoService productoService, CategoriaService categoriaService, AdicionalService adicionalService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.adicionalService = adicionalService;
    }

    // Muestra el menu agrupando los productos disponibles por categoria, por ejemplo Pastas: Pasta boloñesa
    @GetMapping("/menu")
    public String menu(Model model) {
        Collection<Producto> productos = productoService.obtenerDisponibles();

        Map<String, List<Producto>> productosPorCategoria = productos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategoria().getNombre()));

        model.addAttribute("productosPorCategoria", productosPorCategoria);
        return "menu";
    }

    // Muestra el detalle de un producto junto con los adicionales de su categoria
    @GetMapping("/menu/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerPorId(id);

        if (producto == null) {
            return "redirect:/menu";
        }

        Long categoriaId = producto.getCategoria().getId();

        List<Adicional> adicionales = adicionalService.buscarPorCategoria(categoriaId);

        model.addAttribute("producto", producto);
        model.addAttribute("categoria", producto.getCategoria());
        model.addAttribute("adicionales", adicionales);

        return "detalle-producto";
    }

    // Muestra la tabla de todos los productos para el panel de administracion
    @GetMapping("/menu/adminTable")
    public String getMethodName(Model model) {
        model.addAttribute("productos", productoService.obtenerTodos());
        return "adminMenu";
    }
}