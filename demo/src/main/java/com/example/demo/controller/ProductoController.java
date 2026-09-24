package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entities.Producto;
import com.example.demo.service.CategoriaService;
import com.example.demo.service.ProductoService;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.obtenerTodos());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.findAll());
        return "productos/formulario";
    }

    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerPorId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.findAll());
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          @org.springframework.web.bind.annotation.RequestParam(value = "categoriaId", required = false) Long categoriaId) {
        if (categoriaId != null) {
            producto.setCategoria(categoriaService.findById(categoriaId));
        } else if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
            producto.setCategoria(categoriaService.findById(producto.getCategoria().getId()));
        }

        if (producto.getId() != null && productoService.existe(producto.getId())) {
            productoService.actualizar(producto);
        } else {
            productoService.guardar(producto);
        }
        return "redirect:/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }
}