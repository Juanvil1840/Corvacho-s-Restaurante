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

import com.example.demo.dto.AdicionalDTO;
import com.example.demo.dto.MensajeDTO;
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
    public Collection<AdicionalDTO> listar() {
        return adicionalService.findAll()
                .stream()
                .map(AdicionalDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdicionalDTO> buscarPorId(@PathVariable Long id) {
        Adicional adicional = adicionalService.findById(id);
        if (adicional == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AdicionalDTO.fromEntity(adicional));
    }

    @GetMapping("/categoria/{categoriaId}")
    public Collection<AdicionalDTO> buscarPorCategoria(@PathVariable Long categoriaId) {
        return adicionalService.buscarPorCategoria(categoriaId)
                .stream()
                .map(AdicionalDTO::fromEntity)
                .toList();
    }

    @PostMapping
    public ResponseEntity<AdicionalDTO> crear(@RequestBody Adicional adicional) {
        adicional.setId_adicional(null);
        adicional.setCategoria(buscarCategoria(adicional));
        Adicional guardado = adicionalService.guardar(adicional);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdicionalDTO.fromEntity(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdicionalDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Adicional adicional) {
        if (adicionalService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        adicional.setId_adicional(id);
        adicional.setCategoria(buscarCategoria(adicional));
        Adicional actualizado = adicionalService.guardar(adicional);
        return ResponseEntity.ok(AdicionalDTO.fromEntity(actualizado));
    }

        // ⚠️ NUEVOS endpoints de disponibles
    @GetMapping("/disponibles")
    public Collection<AdicionalDTO> listarDisponibles() {
        return adicionalService.findDisponibles()
                .stream()
                .map(AdicionalDTO::fromEntity)
                .toList();
    }

    @GetMapping("/categoria/{categoriaId}/disponibles")
    public Collection<AdicionalDTO> buscarDisponiblesPorCategoria(@PathVariable Long categoriaId) {
        return adicionalService.buscarDisponiblesPorCategoria(categoriaId)
                .stream()
                .map(AdicionalDTO::fromEntity)
                .toList();
    }

    // Cambia el eliminar:
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (adicionalService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        if (adicionalService.tienePedidos(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeDTO(
                            "No se puede eliminar el adicional porque está asociado a uno o más pedidos."));
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
}