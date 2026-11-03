package com.example.demo.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.example.demo.entities.Categoria;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ProductoRepository;

// Implementacion del servicio para la gestion de categorias
@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final AdicionalRepository adicionalRepository;

    // Constructor con inyeccion de repositorios
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, ProductoRepository productoRepository, AdicionalRepository adicionalRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.adicionalRepository = adicionalRepository;
    }

    // Lista todas las categorias registradas
    @Override
    public Collection<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    // Busca una categoria por su id o retorna null si no existe
    @Override
    public Categoria findById(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    // Guarda o actualiza una categoria en la base de datos
    @Override
    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    // Elimina una categoria por su id
    @Override
    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }

    // Comprueba si existe una categoria con el id dado
    @Override
    public boolean existsById(Long id) {
        return categoriaRepository.existsById(id);
    }

    // Verifica si existen productos o adicionales asociados a la categoria antes de eliminarla
    @Override
    public boolean tieneProductosOAdicionales(Long id) {
        return productoRepository.existsByCategoria_Id(id)
                || adicionalRepository.existsByCategoria_Id(id);
    }
}