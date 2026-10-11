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

import com.example.demo.entities.Adicional;
import com.example.demo.entities.Categoria;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.service.AdicionalService;

@RestController
@RequestMapping("/api/adicionales")
@CrossOrigin(origins = "http://localhost:4200")
public class AdicionalRestController {

    private final AdicionalService adicionalService;
    private final CategoriaRepository categoriaRepository;

    public AdicionalRestController(
            AdicionalService adicionalService,
            CategoriaRepository categoriaRepository) {
        this.adicionalService = adicionalService;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public Collection<Map<String, Object>> listar() {
        return adicionalService.findAll()
                .stream()
                .map(this::convertirAdicional)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
        Adicional adicional = adicionalService.findById(id);
        if (adicional == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertirAdicional(adicional));
    }

    @GetMapping("/categoria/{categoriaId}")
    public Collection<Map<String, Object>> buscarPorCategoria(@PathVariable Long categoriaId) {
        return adicionalService.buscarPorCategoria(categoriaId)
                .stream()
                .map(this::convertirAdicional)
                .toList();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Adicional adicional) {
        adicional.setId_adicional(null);
        adicional.setCategoria(buscarCategoria(adicional));
        Adicional guardado = adicionalService.guardar(adicional);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirAdicional(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Long id,
            @RequestBody Adicional adicional) {
        if (adicionalService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        adicional.setId_adicional(id);
        adicional.setCategoria(buscarCategoria(adicional));
        Adicional actualizado = adicionalService.guardar(adicional);
        return ResponseEntity.ok(convertirAdicional(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (adicionalService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        adicionalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Categoria buscarCategoria(Adicional adicional) {
        if (adicional.getCategoria() == null || adicional.getCategoria().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar una categoría");
        }
        return categoriaRepository.findById(adicional.getCategoria().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La categoría seleccionada no existe"));
    }

    private Map<String, Object> convertirAdicional(Adicional adicional) {
        return Map.of(
                "id_adicional", adicional.getId_adicional(),
                "nombre", adicional.getNombre(),
                "precio", adicional.getPrecio(),
                "disponible", adicional.isDisponible(),
                "categoria", Map.of(
                        "id", adicional.getCategoria().getId(),
                        "nombre", adicional.getCategoria().getNombre()
                )
        );
    }
}