package com.example.demo.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resenas", uniqueConstraints = @UniqueConstraint(name = "uk_resena_pedido", columnNames = "id_pedido"))
@Getter
@Setter
@NoArgsConstructor
public class Resena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int puntuacion;

    @Column(length = 1000)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_pedido", nullable = false, unique = true)
    private Pedido pedido;

    @PrePersist
    void asignarFechaCreacion() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
    }
}
