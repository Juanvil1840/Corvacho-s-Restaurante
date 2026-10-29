package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import com.example.demo.entities.Domiciliario;

public interface DomiciliarioService {
    Collection<Domiciliario> findAll();
    Domiciliario findById(Long id);
    Domiciliario guardar(Domiciliario domiciliario);
    Domiciliario crearDomiciliario(Domiciliario domiciliario);
    Domiciliario actualizarDomiciliario(Long id, Domiciliario domiciliario);
    void deleteById(Long id);

    // Domiciliarios que pueden recibir un pedido
    List<Domiciliario> obtenerDisponibles();
}