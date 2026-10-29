package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.DetallePedido;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long>{

    // true si el producto aparece en al menos un pedido (sin importar su estado)
    boolean existsByProducto_Id(Long productoId);

    @Query("SELECT d FROM DetallePedido d WHERE d.pedido.id_Pedido = :id_Pedido")
    List<DetallePedido> findByPedido_Id_Pedido(@Param("id_Pedido") Long id_Pedido);
}
