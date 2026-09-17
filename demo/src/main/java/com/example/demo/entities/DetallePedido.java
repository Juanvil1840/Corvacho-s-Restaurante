package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column( nullable = false)
    int cantidad;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "id_adicional")
    private Adicional adicional;

    public DetallePedido(int cantidad, Producto producto, Pedido pedido, Adicional adicional){
    this.cantidad = cantidad;
    this.producto = producto;
    this.pedido = pedido;
    this.adicional = adicional;
    }
}