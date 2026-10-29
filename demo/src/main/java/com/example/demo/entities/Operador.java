package com.example.demo.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad que representa a un operador encargado de gestionar pedidos y domiciliarios
@Data
@AllArgsConstructor 
@NoArgsConstructor
@Entity 
public class Operador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_operador;
    @Column( length = 100, nullable = false )
    private String nombre;
    @Column( unique = true, length = 100, nullable = false)
    private String usuario;
    @Column( length = 255, nullable = false)
    private String contrasena;

    // Domiciliarios asignados a este operador
    @OneToMany(mappedBy = "operador", cascade = CascadeType.ALL)
    private List<Domiciliario> domiciliarios;

    // Pedidos gestionados por este operador
    @OneToMany(mappedBy = "operador")
    private List<Pedido> pedidos;

    // Constructor para crear un operador con sus credenciales
    public Operador(String nombre, String usuario, String contrasena){
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }
}
