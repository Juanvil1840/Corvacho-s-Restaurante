package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.DetallePedido;

// Repositorio JPA para la entidad DetallePedido
@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long>{

    // true si el producto aparece en al menos un pedido (sin importar su estado)
    boolean existsByProducto_Id(Long productoId);

    // Obtiene todos los detalles asociados a un pedido especifico
    @Query("SELECT d FROM DetallePedido d WHERE d.pedido.id_Pedido = :id_Pedido")
    List<DetallePedido> findByPedido_Id_Pedido(@Param("id_Pedido") Long id_Pedido);

    // Reportes: lineas de los pedidos creados en un rango de fechas. Trae en la misma consulta
    // el pedido, el producto con su categoria y los adicionales (evita el problema N+1)
    @Query("SELECT DISTINCT d FROM DetallePedido d "
            + "JOIN FETCH d.pedido p "
            + "JOIN FETCH d.producto pr "
            + "JOIN FETCH pr.categoria "
            + "LEFT JOIN FETCH d.adicionales "
            + "WHERE p.fechaCreacion >= :desde AND p.fechaCreacion < :hasta")
    List<DetallePedido> findDePedidosCreadosEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
