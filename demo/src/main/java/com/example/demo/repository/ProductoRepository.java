package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
