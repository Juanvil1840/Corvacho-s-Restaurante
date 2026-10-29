package com.example.demo.entities;

import java.util.List;

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

// Entidad que representa a un domiciliario encargado de entregar pedidos
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Domiciliario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_domiciliario;
    @Column( length = 80, nullable = false)
    private String nombre;
    @Column( unique = true, length = 80, nullable = false)
    private String celular;
    @Column( unique = true, length = 80, nullable = false)
    private String cedula;
    // Indica si el domiciliario esta disponible para recibir un pedido
    @Column( nullable = false)
    private boolean informaciondisp;

    // Indica si el domiciliario sigue vinculado activamente al restaurante
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean activo = true;

    // Operador responsable de coordinar a este domiciliario
    @ManyToOne
    @JoinColumn(name = "id_operador")
    private Operador operador;

    // Pedidos asignados a este domiciliario
    @OneToMany(mappedBy = "domiciliario")
    private List<Pedido> pedidos;

    // Constructor para crear un domiciliario con su operador asignado
    public Domiciliario(String nombre, String celular, String cedula, boolean informaciondisp, Operador operador){
    this.nombre = nombre;
    this.celular = celular;
    this.cedula = cedula;
    this.informaciondisp = informaciondisp;
    this.operador = operador;
    }

}
