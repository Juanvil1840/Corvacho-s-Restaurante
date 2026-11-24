package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Resena;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Resena r WHERE r.pedido.id_Pedido = :pedidoId")
    boolean existsByPedidoId(@Param("pedidoId") Long pedidoId);

    @Query("SELECT r FROM Resena r JOIN FETCH r.pedido p JOIN FETCH p.cliente ORDER BY r.fechaCreacion DESC, r.id DESC")
    List<Resena> findAllForAdmin();

    @Query("SELECT r FROM Resena r JOIN FETCH r.pedido p JOIN FETCH p.cliente WHERE p.cliente.clienteId = :clienteId ORDER BY r.fechaCreacion DESC")
    List<Resena> findByClienteId(@Param("clienteId") Long clienteId);
}
