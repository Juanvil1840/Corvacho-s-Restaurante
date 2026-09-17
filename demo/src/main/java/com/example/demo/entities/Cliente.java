package com.example.demo.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor
@Entity  
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clienteId;
    @Column( length = 80, nullable = false)
    private String nombre;
    @Column( length = 80, nullable = false)
    private String apellido;
    @Column( unique = true, length = 80, nullable = false) 
    private String correo;
    @Column( length = 80, nullable = false)
    private String contraseña;
    @Column(length = 80)
    private String telefono;
    @Column(length = 100)
    private String direccion;
    @Column(nullable = false)
    private boolean activo = true;  // Por defecto, el cliente esta activo

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Carrito carrito;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    public Cliente(String nombre, String apellido, String correo, String contraseña, String telefono, String direccion, boolean activo) {
    this.nombre = nombre;
    this.apellido = apellido;
    this.correo = correo;
    this.contraseña = contraseña;
    this.telefono = telefono;
    this.direccion = direccion;
    this.activo = activo;
    }
    
}
