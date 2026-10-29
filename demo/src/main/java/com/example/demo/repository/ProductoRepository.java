package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Producto;

// Repositorio JPA para la entidad Producto
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Obtiene todos los productos marcados como disponibles
    List<Producto> findByDisponibleTrue();

    // Verifica si existen productos asociados a una categoria especifica
    boolean existsByCategoria_Id(Long categoriaId);
}