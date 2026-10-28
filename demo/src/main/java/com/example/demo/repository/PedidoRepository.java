package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Busca todos los pedidos de un cliente específico por su ID
    List<Pedido> findByClienteClienteId(Long clienteId);

    // Busca todos los pedidos cuyo estado sea diferente al indicado (ignorando mayúsculas/minúsculas)
    @Query("SELECT p FROM Pedido p WHERE p.estado IS NULL OR LOWER(p.estado) != LOWER(:estado) ORDER BY p.id_Pedido ASC")
    List<Pedido> findByEstadoNotIgnoreCase(@Param("estado") String estado);
}
