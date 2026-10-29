package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import com.example.demo.entities.Domiciliario;

// Interfaz de servicio para la gestion de domiciliarios
public interface DomiciliarioService {
    // Lista todos los domiciliarios registrados
    Collection<Domiciliario> findAll();
    // Busca un domiciliario por su id
    Domiciliario findById(Long id);
    // Guarda directamente un domiciliario
    Domiciliario guardar(Domiciliario domiciliario);
    // Crea un nuevo domiciliario validando cedula, celular y operador
    Domiciliario crearDomiciliario(Domiciliario domiciliario);
    // Actualiza los datos y estado de un domiciliario existente
    Domiciliario actualizarDomiciliario(Long id, Domiciliario domiciliario);
    // Elimina un domiciliario por su id
    void deleteById(Long id);

    // Domiciliarios que pueden recibir un pedido
    List<Domiciliario> obtenerDisponibles();
}