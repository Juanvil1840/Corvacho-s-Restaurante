package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.ProductoAdicional;

@Repository 
public interface ProductoAdicionalRepository extends JpaRepository<ProductoAdicional,Long> {

    List<ProductoAdicional> findByProductoCategoriaId(Long categoriaId);
}
