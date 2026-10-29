package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Administrador;

// Repositorio JPA para la entidad Administrador
@Repository
public interface AdministradorRepository extends JpaRepository<Administrador,Long> {
    // Busca un administrador por su nombre de usuario
    Administrador findByUsuario(String usuario);
}