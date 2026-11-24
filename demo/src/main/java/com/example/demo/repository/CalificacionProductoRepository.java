package com.example.demo.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.dto.ProductoCalificacionResumenDTO;
import com.example.demo.entities.CalificacionProducto;

public interface CalificacionProductoRepository extends JpaRepository<CalificacionProducto, Long> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CalificacionProducto c "
            + "WHERE c.pedido.id_Pedido = :pedidoId AND c.producto.id = :productoId")
    boolean existsByPedidoAndProducto(@Param("pedidoId") Long pedidoId, @Param("productoId") Long productoId);

    @Query("SELECT c FROM CalificacionProducto c JOIN FETCH c.pedido p JOIN FETCH c.producto "
            + "WHERE p.cliente.clienteId = :clienteId ORDER BY c.fechaCreacion DESC")
    List<CalificacionProducto> findByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT new com.example.demo.dto.ProductoCalificacionResumenDTO(c.producto.id, AVG(c.puntuacion), COUNT(c.id)) "
            + "FROM CalificacionProducto c WHERE c.producto.id IN :productoIds GROUP BY c.producto.id")
    List<ProductoCalificacionResumenDTO> resumirPorProductos(@Param("productoIds") Collection<Long> productoIds);
}
