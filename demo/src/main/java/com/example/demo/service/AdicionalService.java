package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import com.example.demo.entities.Adicional;

public interface AdicionalService {
    Collection<Adicional> findAll();
    Adicional findById(Long id);
    Adicional guardar(Adicional adicional);
    void deleteById(Long id);
    List<Adicional> buscarPorCategoria(Long categoriaId);

    
    List<Adicional> findDisponibles();
    List<Adicional> buscarDisponiblesPorCategoria(Long categoriaId);
    boolean tienePedidos(Long id);
}