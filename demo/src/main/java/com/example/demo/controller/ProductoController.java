package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entities.Producto;
import com.example.demo.service.ProductoService;
import com.example.demo.service.CategoriaService;

@Controller // Controlador
@RequestMapping("/productos") // Cuando se baya a hacer una peticion de este controlador las url deben tener
                              // un /producto
public class ProductoController {

    @Autowired // Inyeccion de dependencias
    private ProductoService productoService; // Utiilizaremos esta variable para llamar a los metodos del servicio

    @Autowired
    private CategoriaService categoriaService; // Utilizamos la variable para llamar a todos los metodos de
                                               // CategoriaService

    // Mostrar la ista con los productos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.obtenerTodos()); // Llama al servicio de productoService, a la
                                                                         // funcion obtener todos
        return "productos/lista"; // Retornamos productos/lista para que se vean (lista.html)
    }

    // Nuevo producto: Cuando se quiera agregar un nuevo plato
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) { // mostramos el formulario para el nuevo producto
        model.addAttribute("producto", new Producto()); // Crea un producto vacio y lo agrega al moedlo
        model.addAttribute("categorias", categoriaService.findAll()); // Cuando creemos un nuevo plato lo tenemos que
                                                                      // clasificar en una nueva categoria, llamamos a
                                                                      // categoriaService para que nos muestre todas
        return "productos/formulario"; // Retornamos el formulario.hmtl
    }

    // Formulario para editar un producto ya existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) { // Extrae el valor del id de la URL y
                                                                                   // lo convierte en un integer
        Producto producto = productoService.obtenerPorId(id); // LLamammos a productosSevice para que obtenga el
                                                              // producto por su id
        if (producto == null) { // Si el producto es null se velve a redirigir
            return "redirect:/productos";
        }
        model.addAttribute("producto", producto); // Si el producto existe lo agrega al modelo para que el formulario
                                                  // muestre sus datos actuales
        model.addAttribute("categorias", categoriaService.findAll()); // Mostramos la categoria en la que esta
        return "productos/formulario";
    }

    // Guardar: Puede que sea guardar un nuevo propducto o guardar los cambios
    // (actualizar) de uno que ya existe
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto) { // Convierte los datos en un objeto producto
        productoService.guardar(producto); // Luego con ayuda de productoService guardamos ese objeto con la nueva
                                           // informacion del formulario
        return "redirect:/productos"; // Redirigimos a productos.html
    }

    // Eliminar un producto
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) { // Obtenemos el id
        productoService.eliminar(id); // Con ayuda del productoSerice, usamos la funcion eliminar y le pasamos por
                                      // parametro el id
        return "redirect:/productos"; // Despues lo redirigimos a productos.html
    }
}