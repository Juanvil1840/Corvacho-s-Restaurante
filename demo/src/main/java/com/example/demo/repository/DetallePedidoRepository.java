package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.DetallePedido;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long>{

    // true si el producto aparece en al menos un pedido (sin importar su estado)
    boolean existsByProducto_Id(Long productoId);
}
