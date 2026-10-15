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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.MensajeDTO;
import com.example.demo.dto.ProductoDTO;
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
    public Collection<ProductoDTO> listar() {
        return productoService.obtenerTodos()
                .stream()
                .map(ProductoDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorId(@PathVariable Long id) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(ProductoDTO.fromEntity(producto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@RequestBody Producto producto) {
        producto.setId(null);
        producto.setCategoria(buscarCategoria(producto));
        Producto guardado = productoService.guardar(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductoDTO.fromEntity(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Producto producto) {
        if (!productoService.existe(id)) {
            return ResponseEntity.notFound().build();
        }

        producto.setId(id);
        producto.setCategoria(buscarCategoria(producto));
        Producto actualizado = productoService.actualizar(producto);
        return ResponseEntity.ok(ProductoDTO.fromEntity(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeDTO> eliminar(@PathVariable Long id) {
        if (!productoService.existe(id)) {
            return ResponseEntity.notFound().build();
        }

        // Solo se permite eliminar si el producto no aparece en ningún pedido
        if (productoService.tienePedidos(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeDTO(
                            "No se puede eliminar el producto porque está asociado a uno o más pedidos."));
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
}