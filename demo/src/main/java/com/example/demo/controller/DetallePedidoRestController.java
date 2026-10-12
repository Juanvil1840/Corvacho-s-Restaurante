package com.example.demo.controller;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.DetallePedido;
import com.example.demo.service.DetallePedidoService;

@RestController
@RequestMapping("/api/detalles-pedido")
@CrossOrigin(origins = "http://localhost:4200")
public class DetallePedidoRestController {

    private final DetallePedidoService detallePedidoService;

    public DetallePedidoRestController(DetallePedidoService detallePedidoService) {
        this.detallePedidoService = detallePedidoService;
    }

    @GetMapping
    public Collection<Map<String, Object>> listar() {
        return detallePedidoService.findAll()
                .stream()
                .map(this::convertirDetalle)
                .toList();
    }

    @GetMapping("/pedido/{pedidoId}")
    public Collection<Map<String, Object>> listarPorPedido(@PathVariable Long pedidoId) {
        return detallePedidoService.findAll()
                .stream()
                .filter(d -> d.getPedido() != null && pedidoId.equals(d.getPedido().getId_Pedido()))
                .map(this::convertirDetalle)
                .toList();
    }

    private Map<String, Object> convertirDetalle(DetallePedido d) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id_DetallePedido", d.getId_DetallePedido());
        map.put("cantidad", d.getCantidad());

        if (d.getProducto() != null) {
            Map<String, Object> prodMap = new LinkedHashMap<>();
            prodMap.put("id", d.getProducto().getId());
            prodMap.put("nombre", d.getProducto().getNombre());
            prodMap.put("precio", d.getProducto().getPrecio());
            prodMap.put("descripcion", d.getProducto().getDescripcion());
            prodMap.put("imagen", d.getProducto().getImagen());
            prodMap.put("disponible", d.getProducto().isDisponible());
            if (d.getProducto().getCategoria() != null) {
                prodMap.put("categoria", Map.of(
                        "id", d.getProducto().getCategoria().getId(),
                        "nombre", d.getProducto().getCategoria().getNombre()
                ));
            }
            map.put("producto", prodMap);
        } else {
            map.put("producto", null);
        }

        if (d.getPedido() != null) {
            Map<String, Object> pedMap = new LinkedHashMap<>();
            pedMap.put("id_Pedido", d.getPedido().getId_Pedido());
            pedMap.put("estado", d.getPedido().getEstado());
            pedMap.put("fechaCreacion", d.getPedido().getFechaCreacion());
            pedMap.put("fechaEntrega", d.getPedido().getFechaEntrega());
            map.put("pedido", pedMap);
        } else {
            map.put("pedido", null);
        }

        if (d.getAdicional() != null) {
            Map<String, Object> adicMap = new LinkedHashMap<>();
            adicMap.put("id_adicional", d.getAdicional().getId_adicional());
            adicMap.put("nombre", d.getAdicional().getNombre());
            adicMap.put("precio", d.getAdicional().getPrecio());
            adicMap.put("disponible", d.getAdicional().isDisponible());
            map.put("adicional", adicMap);
        } else {
            map.put("adicional", null);
        }

        return map;
    }
}

