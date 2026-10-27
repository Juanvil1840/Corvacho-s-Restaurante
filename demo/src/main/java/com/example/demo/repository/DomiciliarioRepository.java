package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Domiciliario;

@Repository
public interface DomiciliarioRepository extends JpaRepository<Domiciliario, Long>{

    // Usados para validar que no se repitan cédula ni celular
    Optional<Domiciliario> findByCedula(String cedula);
    Optional<Domiciliario> findByCelular(String celular);

    // Trae solo los que están disponibles (activos)
    List<Domiciliario> findByInformaciondispTrue();
}