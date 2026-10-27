package com.example.demo.service;

import java.util.Collection;

import com.example.demo.dto.ActualizarCarritoDTO;
import com.example.demo.dto.CarritoDTO;
import com.example.demo.entities.Carrito;

public interface CarritoService {
    Collection<Carrito> findAll();
    Carrito findById(Long id);
    Carrito guardar(Carrito carrito);
    void deleteById(Long id);
    CarritoDTO obtenerPorCliente(Long clienteId);
    CarritoDTO actualizarPorCliente(Long clienteId, ActualizarCarritoDTO carrito);
    void vaciarPorCliente(Long clienteId);
}
