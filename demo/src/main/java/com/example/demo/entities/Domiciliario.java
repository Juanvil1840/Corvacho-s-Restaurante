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
    @Column( nullable = false)
    private boolean informaciondisp;

    @ManyToOne
    @JoinColumn(name = "id_operador")
    private Operador operador;

    @OneToMany(mappedBy = "domiciliario")
    private List<Pedido> pedidos;

    public Domiciliario(String nombre, String celular, String cedula, boolean informaciondisp, Operador operador){
    this.nombre = nombre;
    this.celular = celular;
    this.cedula = cedula;
    this.informaciondisp = informaciondisp;
    this.operador = operador;
    }

}
