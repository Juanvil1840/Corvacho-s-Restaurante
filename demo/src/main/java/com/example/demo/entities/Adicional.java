package com.example.demo.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor 
@Entity
public class Adicional{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id_adicional;
    @Column( length = 80, nullable = false)
    private String nombre;
    @Column( nullable = false)
    private double precio;
    @Column( nullable = false)
    private boolean disponible;

    @OneToMany(mappedBy = "adicional")
    private List<ProductoAdicional> productoAdicionales;

    @OneToMany(mappedBy = "adicional")
    private List<DetallePedido> detallePedidos;

    public Adicional(String nombre, double precio, boolean disponible){
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
    }
}
