package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Domiciliario;

@Repository
public interface DomiciliarioRepository extends JpaRepository<Domiciliario, Long>{
    
}
