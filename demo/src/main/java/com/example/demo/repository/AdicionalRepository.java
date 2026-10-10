package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Adicional;

@Repository 
public interface AdicionalRepository extends JpaRepository<Adicional,Long>{
    List<Adicional> findByCategoriaId(Long categoriaId);
}
