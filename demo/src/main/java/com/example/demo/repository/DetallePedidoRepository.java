package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.DetallePedido;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long>{

    // true si el producto aparece en al menos un pedido (sin importar su estado)
    boolean existsByProducto_Id(Long productoId);

    @Query("SELECT d FROM DetallePedido d WHERE d.pedido.id_Pedido = :id_Pedido")
    java.util.List<DetallePedido> findByPedido_Id_Pedido(@Param("id_Pedido") Long id_Pedido);

    @Modifying
    @Transactional
    @Query("DELETE FROM DetallePedido d WHERE d.pedido.id_Pedido = :id_Pedido")
    void deleteByPedido_Id_Pedido(@Param("id_Pedido") Long id_Pedido);
}
