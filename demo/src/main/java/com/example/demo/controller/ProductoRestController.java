package com.example.demo.controller;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.MensajeDTO;
import com.example.demo.dto.ProductoDTO;
import com.example.demo.entities.Producto;
import com.example.demo.service.ProductoService;

// Controlador REST para la gestion de Productos
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoRestController {

    private final ProductoService productoService;

    public ProductoRestController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // Lista los productos (por defecto todos=true, con todos=false solo disponibles para el menú)
    @GetMapping
    public Collection<ProductoDTO> listar(
            @RequestParam(value = "todos", required = false, defaultValue = "true") boolean todos) {
        Collection<Producto> productos = todos
                ? productoService.obtenerTodos()
                : productoService.obtenerDisponibles();

        return productos
                .stream()
                .map(ProductoDTO::fromEntity)
                .toList();
    }

    // Busca un producto por su id
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorId(@PathVariable Long id) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(ProductoDTO.fromEntity(producto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Crea un nuevo producto en la base de datos
    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@RequestBody Producto producto) {
        Producto guardado = productoService.crearProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductoDTO.fromEntity(guardado));
    }

    // Actualiza los datos de un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Producto producto) {
        Producto actualizado = productoService.actualizarProducto(id, producto);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ProductoDTO.fromEntity(actualizado));
    }

    // Elimina un producto de la base de datos
    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeDTO> eliminar(@PathVariable Long id) {
        if (!productoService.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
