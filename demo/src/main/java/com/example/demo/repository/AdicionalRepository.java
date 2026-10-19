package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Adicional;

@Repository 
public interface AdicionalRepository extends JpaRepository<Adicional, Long>{

    List<Adicional> findByCategoriaId(Long categoriaId);

    List<Adicional> findByDisponibleTrue();

    List<Adicional> findByCategoriaIdAndDisponibleTrue(Long categoriaId);

    boolean existsByCategoria_Id(Long categoriaId);

    @Query("SELECT COUNT(d) > 0 FROM DetallePedido d WHERE d.adicional.id_adicional = :adicionalId")
    boolean existsEnDetallePedido(@Param("adicionalId") Long adicionalId);
}