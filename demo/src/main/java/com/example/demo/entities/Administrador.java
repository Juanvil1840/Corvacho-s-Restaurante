package com.example.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad que representa a un usuario administrador del sistema
@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Entity
public class Administrador {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_administrador;

    @Column (length = 80, nullable = false)
    private String nombre;

    @Column (unique = true, length = 100, nullable = false)
    private String usuario;

    @Column (length = 255, nullable = false)
    private String contrasena;
    
    // Constructor para crear un administrador con sus credenciales
    public Administrador(String nombre, String usuario, String contrasena){
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }
}