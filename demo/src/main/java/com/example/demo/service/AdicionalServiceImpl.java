package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Adicional;
import com.example.demo.entities.Categoria;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.CategoriaRepository;

// Implementacion del servicio para la gestion de adicionales
@Service
public class AdicionalServiceImpl implements AdicionalService {

    private final AdicionalRepository adicionalRepository;
    private final CategoriaRepository categoriaRepository;

    // Constructor con inyeccion de repositorios
    public AdicionalServiceImpl(AdicionalRepository adicionalRepository, CategoriaRepository categoriaRepository) {
        this.adicionalRepository = adicionalRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // Lista todos los adicionales registrados
    @Override
    public Collection<Adicional> findAll() {
        return adicionalRepository.findAll();
    }

    // Busca un adicional por su id o retorna null si no existe
    @Override
    public Adicional findById(Long id) {
        return adicionalRepository.findById(id).orElse(null);
    }

    // Guarda un adicional en la base de datos
    @Override
    public Adicional guardar(Adicional adicional) {
        return adicionalRepository.save(adicional);
    }

    // Crea un nuevo adicional asegurando que tenga una categoria valida
    @Override
    public Adicional crearAdicional(Adicional adicional) {
        adicional.setId_adicional(null);
        adicional.setCategoria(buscarCategoria(adicional));
        return adicionalRepository.save(adicional);
    }

    // Actualiza un adicional existente y su categoria asociada
    @Override
    public Adicional actualizarAdicional(Long id, Adicional adicional) {
        if (findById(id) == null) {
            return null;
        }
        adicional.setId_adicional(id);
        adicional.setCategoria(buscarCategoria(adicional));
        return adicionalRepository.save(adicional);
    }

    // Elimina un adicional por su id
    @Override
    public void deleteById(Long id) {
        adicionalRepository.deleteById(id);
    }

    // Busca los adicionales que pertenecen a una categoria
    @Override
    public List<Adicional> buscarPorCategoria(Long categoriaId) {
        return adicionalRepository.findByCategoriaId(categoriaId);
    }

    // Lista unicamente los adicionales marcados como disponibles
    @Override
    public List<Adicional> findDisponibles() {
        return adicionalRepository.findByDisponibleTrue();
    }

    // Lista los adicionales disponibles de una categoria especifica
    @Override
    public List<Adicional> buscarDisponiblesPorCategoria(Long categoriaId) {
        return adicionalRepository.findByCategoriaIdAndDisponibleTrue(categoriaId);
    }

    // Comprueba si el adicional ha sido incluido en algun pedido
    @Override
    public boolean tienePedidos(Long id) {
        return adicionalRepository.existsEnDetallePedido(id);
    }

    // Comprueba si el adicional se encuentra actualmente en algun carrito
    @Override
    public boolean estaEnCarrito(Long id) {
        return adicionalRepository.existsEnCarrito(id);
    }

    // Valida y obtiene la categoria seleccionada para el adicional
    private Categoria buscarCategoria(Adicional adicional) {
        if (adicional.getCategoria() == null || adicional.getCategoria().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar una categoría");
        }
        return categoriaRepository.findById(adicional.getCategoria().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La categoría seleccionada no existe"));
    }
}
