package com.example.demo.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad que representa un pedido realizado por un cliente
@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Entity 
public class Pedido {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_Pedido;

    @Column(length = 50, nullable = false)
    private String estado = "Recibido";

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = true) // Puede ser null mientras el pedido no ha sido entregado
    private LocalDateTime fechaEntrega;

    // Cliente que realizo el pedido
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    // Operador que gestiona el pedido
    @ManyToOne
    @JoinColumn(name = "id_operador")
    private Operador operador;

    // Domiciliario asignado para la entrega
    @ManyToOne
    @JoinColumn(name = "id_domiciliario")
    private Domiciliario domiciliario;

    // Detalles o lineas de productos que componen el pedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detallePedidos;

    // Cupon de descuento usado en el pedido (null si no uso ninguno)
    @ManyToOne
    @JoinColumn(name = "id_cupon")
    private Cupon cupon;

    // Valor descontado por el cupon, calculado por el backend al crear el pedido (null o 0 = sin descuento)
    @Column(nullable = true)
    private Double descuento;

    // Codigo que escribio el cliente al hacer el pedido; no se guarda en la tabla, solo llega en la peticion
    @Transient
    private String codigoCupon;

    // Constructor para crear un pedido asegurando el estado inicial "Recibido" por defecto
    public Pedido(String estado, LocalDateTime fechaCreacion, LocalDateTime fechaEntrega, Cliente cliente, Operador operador, Domiciliario domiciliario) {
        this.estado = (estado != null && !estado.trim().isEmpty()) ? estado : "Recibido";
        this.fechaCreacion = fechaCreacion;
        this.fechaEntrega = fechaEntrega;
        this.cliente = cliente;
        this.operador = operador;
        this.domiciliario = domiciliario;
    }

    // Descuento como numero (0 si no tiene)
    public double valorDescuento() {
        return descuento != null ? descuento : 0;
    }
}
