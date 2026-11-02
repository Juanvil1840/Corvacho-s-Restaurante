package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Pedido;

// Repositorio JPA para la entidad Pedido
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Busca todos los pedidos de un cliente específico por su ID
    List<Pedido> findByClienteClienteId(Long clienteId);

    // Busca todos los pedidos cuyo estado sea diferente al indicado (ignorando mayúsculas/minúsculas)
    @Query("SELECT p FROM Pedido p WHERE p.estado IS NULL OR LOWER(p.estado) != LOWER(:estado) ORDER BY p.id_Pedido ASC")
    List<Pedido> findByEstadoNotIgnoreCase(@Param("estado") String estado);

    // Busca el historial de pedidos de un cliente y los ordena por fecha e ID descendentes
    @Query("SELECT p FROM Pedido p WHERE p.cliente.clienteId = :clienteId ORDER BY p.fechaCreacion DESC, p.id_Pedido DESC")
    List<Pedido> findHistorialByCliente(@Param("clienteId") Long clienteId);

    // Busca pedidos por estado ignorando mayúsculas y minúsculas, ordenados por fecha e ID descendentes
    @Query("SELECT p FROM Pedido p WHERE LOWER(p.estado) = LOWER(:estado) ORDER BY p.fechaCreacion DESC, p.id_Pedido DESC")
    List<Pedido> findByEstadoIgnoreCase(@Param("estado") String estado);
}
