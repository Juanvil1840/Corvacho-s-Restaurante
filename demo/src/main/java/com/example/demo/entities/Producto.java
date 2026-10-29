package com.example.demo.entities;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// Entidad que representa un plato o producto ofrecido en el menu del restaurante
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Producto {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column( length = 80, nullable = false)
    private String nombre;
    @Column(nullable = false)
    private double precio;
    @Column( columnDefinition = "TEXT", nullable = false)
    private String descripcion;
    @Column( length = 255, nullable = false)
    private String imagen;
    @Column( nullable = false)
    private boolean disponible;
    // Categoria a la que pertenece el producto
    @ManyToOne
    @JoinColumn( name = "id_categoria", nullable = false) 
    private Categoria categoria;

    // Referencias a los carritos donde se encuentra este producto
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<ProductoCarrito> productoCarritos;

    // Referencias a los detalles de pedidos donde se ha solicitado este producto
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detallePedidos;

    // Constructor para crear un producto con todos sus datos y su categoria
    public Producto(String nombre, double precio, String descripcion, String imagen, boolean disponible, Categoria categoria) {
    this.nombre = nombre;
    this.precio = precio;
    this.descripcion = descripcion;
    this.imagen = imagen;
    this.disponible = disponible;
    this.categoria = categoria;
    }
}