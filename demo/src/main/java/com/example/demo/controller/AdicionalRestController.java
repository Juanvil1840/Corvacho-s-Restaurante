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

import com.example.demo.dto.AdicionalDTO;
import com.example.demo.dto.MensajeDTO;
import com.example.demo.entities.Adicional;
import com.example.demo.service.AdicionalService;

// Controlador REST, para la gestion de Adicionales
@RestController
@RequestMapping("/api/adicionales")
@CrossOrigin(origins = "http://localhost:4200")
public class AdicionalRestController {

    private final AdicionalService adicionalService;

    public AdicionalRestController(AdicionalService adicionalService) {
        this.adicionalService = adicionalService;
    }

    // Lista todos los adicionales registrados y los convierte a DTO
    @GetMapping
    public Collection<AdicionalDTO> listar() {
        return adicionalService.findAll()
                .stream()
                .map(AdicionalDTO::fromEntity)
                .toList();
    }

    // Lista unicamente los adicionales que estan disponibles
    @GetMapping("/disponibles")
    public Collection<AdicionalDTO> listarDisponibles() {
        return adicionalService.findDisponibles()
                .stream()
                .map(AdicionalDTO::fromEntity)
                .toList();
    }

    // Busca un adicional por su ID
    @GetMapping("/{id}")
    public ResponseEntity<AdicionalDTO> buscarPorId(@PathVariable Long id) {
        Adicional adicional = adicionalService.findById(id);
        if (adicional == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AdicionalDTO.fromEntity(adicional));
    }

    //Lista los adicionales asociados a una categoria especifica
    @GetMapping("/categoria/{categoriaId}")
    public Collection<AdicionalDTO> buscarPorCategoria(@PathVariable Long categoriaId) {
        return adicionalService.buscarPorCategoria(categoriaId)
                .stream()
                .map(AdicionalDTO::fromEntity)
                .toList();
    }

    // Lista los adicionales disponibles asociados a una categoria especifica
    @GetMapping("/categoria/{categoriaId}/disponibles")
    public Collection<AdicionalDTO> buscarDisponiblesPorCategoria(@PathVariable Long categoriaId) {
        return adicionalService.buscarDisponiblesPorCategoria(categoriaId)
                .stream()
                .map(AdicionalDTO::fromEntity)
                .toList();
    }

    // Crea un nuevo adicional, el servicio valida que la categoria exista
    @PostMapping
    public ResponseEntity<AdicionalDTO> crear(@RequestBody Adicional adicional) {
        Adicional guardado = adicionalService.crearAdicional(adicional);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdicionalDTO.fromEntity(guardado));
    }

    // Actualiza un adicional existente
    @PutMapping("/{id}")
    public ResponseEntity<AdicionalDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Adicional adicional) {
        Adicional actualizado = adicionalService.actualizarAdicional(id, adicional);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AdicionalDTO.fromEntity(actualizado));
    }

    // Elimina un adicional, si no esta asociado a ningun pedido y si lo esta, manda un mensaje que no se puede eliminar porque esta asociado a un pedido
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (adicionalService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        if (adicionalService.tienePedidos(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeDTO(
                            "No se puede eliminar el adicional porque esta asociado a uno o mas pedidos."));
        }

        adicionalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
