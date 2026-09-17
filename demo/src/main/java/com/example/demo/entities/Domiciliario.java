package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Boolean informaciondisp;

}
