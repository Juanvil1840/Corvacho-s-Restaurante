package com.example.demo.entities;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private double precio;

    @Column(length = 500)
    private String descripcion;

    @Column(length = 500)
    private String imagen;

    @Column(nullable = false)
    private boolean disponible;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "producto_ingredientes", joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "ingrediente", length = 100)
    private List<String> ingredientes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Métodos de compatibilidad para idCategoria
    public Integer getIdCategoria() {
        return categoria != null ? categoria.getId() : null;
    }

    public void setIdCategoria(Integer idCategoria) {
        if (idCategoria != null) {
            Categoria cat = new Categoria();
            cat.setId(idCategoria);
            this.categoria = cat;
        } else {
            this.categoria = null;
        }
    }
}