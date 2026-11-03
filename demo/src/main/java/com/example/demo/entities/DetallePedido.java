package com.example.demo.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad que representa una linea de producto y sus adicionales dentro de un pedido
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_DetallePedido;

    @Column(nullable = false)
    private int cantidad;

    // Producto solicitado en esta linea del pedido
    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    // Pedido al que pertenece este detalle
    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    // Lista de adicionales seleccionados para este producto
    @ElementCollection(fetch = FetchType.EAGER)
    private List<AdicionalSeleccionado> adicionales = new ArrayList<>();

    // Constructor para crear un detalle de pedido con un adicional inicial opcional
    public DetallePedido(int cantidad, Producto producto, Pedido pedido, Adicional adicional) {
        this.cantidad = cantidad;
        this.producto = producto;
        this.pedido = pedido;
        if (adicional != null) {
            this.adicionales.add(new AdicionalSeleccionado(
                    adicional.getId_adicional(),
                    adicional.getNombre(),
                    adicional.getPrecio(),
                    1
            ));
        }
    }
}
