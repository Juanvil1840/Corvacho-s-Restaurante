package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Operador;

// Repositorio JPA para la entidad Operador
@Repository 
public interface OperadorRepository extends JpaRepository<Operador, Long> {
    
    // Busca un operador por su nombre de usuario
    Operador findByUsuario(String usuario);
}