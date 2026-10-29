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

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<AdicionalSeleccionado> adicionales = new ArrayList<>();

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
