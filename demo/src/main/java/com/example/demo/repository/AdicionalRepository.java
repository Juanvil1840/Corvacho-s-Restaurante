package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Adicional;

// Repositorio JPA para la entidad Adicional
@Repository 
public interface AdicionalRepository extends JpaRepository<Adicional, Long>{

    // Obtiene todos los adicionales de una categoria especifica
    List<Adicional> findByCategoriaId(Long categoriaId);

    // Obtiene todos los adicionales que estan disponibles
    List<Adicional> findByDisponibleTrue();

    // Obtiene los adicionales disponibles de una categoria especifica
    List<Adicional> findByCategoriaIdAndDisponibleTrue(Long categoriaId);

    // Verifica si existen adicionales asociados a una categoria
    boolean existsByCategoria_Id(Long categoriaId);

    // Comprueba si el adicional esta presente en algun detalle de pedido
    @Query("SELECT COUNT(d) > 0 FROM DetallePedido d JOIN d.adicionales a WHERE a.id_adicional = :adicionalId")
    boolean existsEnDetallePedido(@Param("adicionalId") Long adicionalId);

    // Comprueba si el adicional esta presente en el carrito de algun cliente
    @Query("SELECT COUNT(p) > 0 FROM ProductoCarrito p JOIN p.adicionales a WHERE a.id_adicional = :adicionalId")
    boolean existsEnCarrito(@Param("adicionalId") Long adicionalId);
}