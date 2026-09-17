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

    public ProductoCarrito(int cantidadP){
        this.cantidadP = cantidadP;
    }
    
}
