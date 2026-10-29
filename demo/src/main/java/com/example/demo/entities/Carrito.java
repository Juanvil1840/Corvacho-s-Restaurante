package com.example.demo.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad que representa el carrito de compras asociado a un cliente
@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Entity
public class Carrito {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_carrito;
    
    // Cliente dueno del carrito
    @OneToOne 
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    // Lista de productos agregados al carrito
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoCarrito> productoCarritos = new java.util.ArrayList<>();

    // Constructor que asocia el carrito a un cliente
    public Carrito(Cliente cliente){
    this.cliente = cliente;
    }
}
