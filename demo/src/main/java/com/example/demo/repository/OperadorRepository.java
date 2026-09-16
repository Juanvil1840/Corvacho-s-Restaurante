package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Operador;

@Repository 
public interface OperadorRepository extends JpaRepository<Operador,Long>{
    
}
