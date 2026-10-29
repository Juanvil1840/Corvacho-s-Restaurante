package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Carrito;

// Repositorio JPA para la entidad Carrito
@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long>{
    // Busca el carrito perteneciente a un cliente especifico
    Optional<Carrito> findByClienteClienteId(Long clienteId);
}
