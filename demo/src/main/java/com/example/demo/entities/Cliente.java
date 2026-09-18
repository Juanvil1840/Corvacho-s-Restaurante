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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "El nombre es obligatorio") //Para que el nombre no este vacio
    @Column(length = 80, nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Column(length = 80, nullable = false)
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email //Verificacion del formato correcto del email
    @Column(unique = true, length = 80, nullable = false)
    private String correo;

    @NotBlank(message = "La contrasena es obligatoria")
    @Column(length = 80, nullable = false)
    private String contraseña;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @NotBlank(message = "La direccion es obligatoria")
    @Column(length = 100)
    private String direccion;

    @Column(nullable = false)
    private boolean activo = true;

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