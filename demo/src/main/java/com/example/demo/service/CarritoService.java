package com.example.demo.service;

import java.util.Collection;

import com.example.demo.dto.CarritoDTO;
import com.example.demo.entities.Carrito;

// Interfaz de servicio para la gestion del carrito de compras
public interface CarritoService {
    // Lista todos los carritos registrados
    Collection<Carrito> findAll();
    // Busca un carrito por su id
    Carrito findById(Long id);
    // Guarda un carrito en la base de datos
    Carrito guardar(Carrito carrito);
    // Elimina un carrito por su id
    void deleteById(Long id);
    // Obtiene el carrito de un cliente en formato DTO
    CarritoDTO obtenerPorCliente(Long clienteId);
    // Actualiza los productos y adicionales del carrito de un cliente
    CarritoDTO actualizarPorCliente(Long clienteId, CarritoDTO carrito);
    // Elimina todos los items del carrito de un cliente
    void vaciarPorCliente(Long clienteId);
}
