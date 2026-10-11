package com.example.demo.controller;

import java.util.Collection;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Categoria;
import com.example.demo.entities.Producto;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoRestController {

    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;

    public ProductoRestController(
            ProductoService productoService,
            CategoriaRepository categoriaRepository) {
        this.productoService = productoService;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public Collection<Map<String, Object>> listar() {
        return productoService.obtenerTodos()
                .stream()
                .map(this::convertirProducto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(
            @PathVariable Long id) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(convertirProducto(producto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(
            @RequestBody Producto producto) {

        producto.setId(null);
        producto.setCategoria(buscarCategoria(producto));

        Producto guardado = productoService.guardar(producto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertirProducto(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Long id,
            @RequestBody Producto producto) {

        if (!productoService.existe(id)) {
            return ResponseEntity.notFound().build();
        }

        producto.setId(id);
        producto.setCategoria(buscarCategoria(producto));

        Producto actualizado = productoService.actualizar(producto);

        return ResponseEntity.ok(convertirProducto(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        if (!productoService.existe(id)) {
            return ResponseEntity.notFound().build();
        }

        productoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    private Categoria buscarCategoria(Producto producto) {
        if (producto.getCategoria() == null
                || producto.getCategoria().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar una categoría");
        }

        return categoriaRepository.findById(producto.getCategoria().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La categoría seleccionada no existe"));
    }

    private Map<String, Object> convertirProducto(Producto producto) {
        return Map.of(
                "id", producto.getId(),
                "nombre", producto.getNombre(),
                "precio", producto.getPrecio(),
                "descripcion", producto.getDescripcion(),
                "imagen", producto.getImagen(),
                "disponible", producto.isDisponible(),
                "categoria", Map.of(
                        "id", producto.getCategoria().getId(),
                        "nombre", producto.getCategoria().getNombre()
                )
        );
    }
}