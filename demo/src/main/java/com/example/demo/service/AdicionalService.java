package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import com.example.demo.entities.Adicional;

// Interfaz de servicio para la logica de negocio de los adicionales
public interface AdicionalService {
    // Obtiene todos los adicionales registrados
    Collection<Adicional> findAll();
    // Busca un adicional por su identificador
    Adicional findById(Long id);
    // Guarda un adicional directamente en la base de datos
    Adicional guardar(Adicional adicional);
    // Crea un nuevo adicional validando su categoria
    Adicional crearAdicional(Adicional adicional);
    // Actualiza los datos de un adicional existente
    Adicional actualizarAdicional(Long id, Adicional adicional);
    // Elimina un adicional por su id
    void deleteById(Long id);
    // Busca todos los adicionales asociados a una categoria
    List<Adicional> buscarPorCategoria(Long categoriaId);

    // Obtiene solo los adicionales disponibles
    List<Adicional> findDisponibles();
    // Obtiene los adicionales disponibles de una categoria especifica
    List<Adicional> buscarDisponiblesPorCategoria(Long categoriaId);
    // Verifica si el adicional esta presente en algun pedido
    boolean tienePedidos(Long id);

    // Verifica si el adicional esta presente en algun carrito de compras
    boolean estaEnCarrito(Long id);
}
