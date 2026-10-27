package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.Producto;
import com.example.demo.service.CategoriaService;
import com.example.demo.service.ProductoService;

// Controlador para las vistas de Productos
@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController( ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    // Muestra la lista de todos los productos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.obtenerTodos());
        return "productos/lista";
    }

    // Muestra el formulario para crear un nuevo producto
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.findAll());
        return "productos/formulario";
    }

    // Muestra el formulario para editar un producto existente por su id
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model model) {
        Producto producto = productoService.obtenerPorId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.findAll());
        return "productos/formulario";
    }

    // Guarda o actualiza un producto
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Producto producto,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId) {

        if (categoriaId != null) {
            producto.setCategoria(categoriaService.findById(categoriaId));
        } else if (producto.getCategoria() != null
                && producto.getCategoria().getId() != null) {
            producto.setCategoria(
                    categoriaService.findById(producto.getCategoria().getId()));
        }

        if (producto.getId() != null && productoService.existe(producto.getId())) {
            productoService.actualizar(producto);
        } else {
            productoService.guardar(producto);
        }

        return "redirect:/productos";
    }

    // Elimina un producto por su id
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }
}