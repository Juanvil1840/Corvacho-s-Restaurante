package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long>{
    Optional<Carrito> findByClienteClienteId(Long clienteId);
}
