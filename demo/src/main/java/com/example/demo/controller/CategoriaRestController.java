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

import com.example.demo.dto.CategoriaDTO;
import com.example.demo.dto.MensajeDTO;
import com.example.demo.entities.Categoria;
import com.example.demo.service.CategoriaService;
import com.example.demo.dto.MensajeDTO;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoriaRestController {

    private final CategoriaService categoriaService;

    public CategoriaRestController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public Collection<CategoriaDTO> listar() {
        return categoriaService.findAll()
                .stream()
                .map(CategoriaDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CategoriaDTO.fromEntity(categoria));
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> crear(@RequestBody Categoria categoria) {
        categoria.setId(null);
        Categoria guardada = categoriaService.guardar(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoriaDTO.fromEntity(guardada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Categoria categoria) {
        if (!categoriaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoria.setId(id);
        Categoria actualizada = categoriaService.guardar(categoria);
        return ResponseEntity.ok(CategoriaDTO.fromEntity(actualizada));
    }

      @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!categoriaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (categoriaService.tieneProductosOAdicionales(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeDTO(
                            "No se puede eliminar la categoría porque tiene productos o adicionales asociados."));
        }

        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}