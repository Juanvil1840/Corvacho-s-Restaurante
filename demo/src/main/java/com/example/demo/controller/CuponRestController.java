package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

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

import com.example.demo.dto.CuponDTO;
import com.example.demo.dto.CuponValidadoDTO;
import com.example.demo.entities.Cupon;
import com.example.demo.service.CuponService;

// Controlador REST para los cupones de descuento.
// El administrador los gestiona; el cliente solo puede probar un codigo en su carrito (/validar)
@RestController
@RequestMapping("/api/cupones")
@CrossOrigin(origins = "http://localhost:4200")
public class CuponRestController {

    private final CuponService cuponService;

    public CuponRestController(CuponService cuponService) {
        this.cuponService = cuponService;
    }

    // Lista todos los cupones con su estado y cuantas veces se han usado
    @GetMapping
    public List<CuponDTO> listar() {
        LocalDate hoy = LocalDate.now();
        return cuponService.findAll().stream()
                .map(cupon -> CuponDTO.fromEntity(cupon, hoy))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuponDTO> buscarPorId(@PathVariable Long id) {
        Cupon cupon = cuponService.findById(id);
        if (cupon == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CuponDTO.fromEntity(cupon, LocalDate.now()));
    }

    // El cliente prueba un codigo en el carrito y ve cuanto le descuenta.
    // Es solo una vista previa: al crear el pedido el backend vuelve a validar con los precios reales
    @GetMapping("/validar")
    public CuponValidadoDTO validar(@RequestParam String codigo, @RequestParam double subtotal) {
        Cupon cupon = cuponService.validar(codigo, subtotal);
        double descuento = cuponService.calcularDescuento(cupon, subtotal);
        return new CuponValidadoDTO(cupon.getCodigo(), cupon.getDescripcion(), cupon.getTipo(), cupon.getValor(),
                subtotal, descuento, subtotal - descuento);
    }

    @PostMapping
    public ResponseEntity<CuponDTO> crear(@RequestBody Cupon cupon) {
        Cupon guardado = cuponService.crear(cupon);
        return ResponseEntity.status(HttpStatus.CREATED).body(CuponDTO.fromEntity(guardado, LocalDate.now()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuponDTO> actualizar(@PathVariable Long id, @RequestBody Cupon cupon) {
        Cupon actualizado = cuponService.actualizar(id, cupon);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CuponDTO.fromEntity(actualizado, LocalDate.now()));
    }

    // Si el cupon ya se uso en pedidos responde 409 con el mensaje (el servicio lanza el error)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (cuponService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        cuponService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
