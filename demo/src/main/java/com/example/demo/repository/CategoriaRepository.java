package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Categoria;

// Repositorio JPA para las operaciones CRUD de la entidad Categoria
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

}