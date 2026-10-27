package com.example.demo.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Entity;
import jakarta.persistence.ElementCollection;
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
public class ProductoCarrito {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_ProductoCarrito;
    @Column( nullable = false)
    private int cantidadP;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_carrito")
    private Carrito carrito;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "producto_carrito_adicionales", joinColumns = @JoinColumn(name = "id_producto_carrito"))
    private List<AdicionalSeleccionado> adicionales = new ArrayList<>();

    public ProductoCarrito(int cantidadP, Producto producto, Carrito carrito){
    this.cantidadP = cantidadP;
    this.producto = producto;
    this.carrito = carrito;
    }
}
