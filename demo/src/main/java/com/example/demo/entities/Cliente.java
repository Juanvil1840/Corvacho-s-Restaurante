package com.example.demo.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad que representa a un cliente registrado en el restaurante
@Data
@AllArgsConstructor 
@NoArgsConstructor  
@Entity  
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clienteId;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(length = 80, nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Column(length = 80, nullable = false)
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email
    @Column(unique = true, length = 80, nullable = false)
    private String correo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "La contrasena es obligatoria")
    @Column(name = "contraseña", length = 80, nullable = false)
    private String contrasena;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @NotBlank(message = "La direccion es obligatoria")
    @Column(length = 100)
    private String direccion;

    @Column(nullable = false)
    private boolean activo = true;

    // Carrito de compras asociado al cliente
    @JsonIgnore
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Carrito carrito;

    // Historial de pedidos realizados por el cliente
    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    // Usuario de seguridad (tabla UserEntity) con el que inicia sesion
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // Constructor para registrar un cliente con sus datos personales
    public Cliente(String nombre, String apellido, String correo, String contrasena, String telefono, String direccion, boolean activo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = activo;
    }
}