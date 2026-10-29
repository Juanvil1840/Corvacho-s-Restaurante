package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.ProductoCarrito;

// Repositorio JPA para gestionar los items de productos dentro de un carrito
@Repository
public interface ProductoCarritoRepository extends JpaRepository<ProductoCarrito, Long> {
    
}
