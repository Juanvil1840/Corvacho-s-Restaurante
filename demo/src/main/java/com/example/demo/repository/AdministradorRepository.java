package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Administrador;

public interface AdministradorRepository extends JpaRepository<Administrador,Long> {
    
}
