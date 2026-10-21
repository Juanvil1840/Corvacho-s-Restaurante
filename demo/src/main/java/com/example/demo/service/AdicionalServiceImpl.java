package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Adicional;
import com.example.demo.entities.Categoria;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.CategoriaRepository;

@Service
public class AdicionalServiceImpl implements AdicionalService {

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public Collection<Adicional> findAll() {
        return adicionalRepository.findAll();
    }

    @Override
    public Adicional findById(Long id) {
        return adicionalRepository.findById(id).orElse(null);
    }

    @Override
    public Adicional guardar(Adicional adicional) {
        return adicionalRepository.save(adicional);
    }

    @Override
    public Adicional crearAdicional(Adicional adicional) {
        adicional.setId_adicional(null);
        adicional.setCategoria(buscarCategoria(adicional));
        return adicionalRepository.save(adicional);
    }

    @Override
    public Adicional actualizarAdicional(Long id, Adicional adicional) {
        if (findById(id) == null) {
            return null;
        }
        adicional.setId_adicional(id);
        adicional.setCategoria(buscarCategoria(adicional));
        return adicionalRepository.save(adicional);
    }

    @Override
    public void deleteById(Long id) {
        adicionalRepository.deleteById(id);
    }

    @Override
    public List<Adicional> buscarPorCategoria(Long categoriaId) {
        return adicionalRepository.findByCategoriaId(categoriaId);
    }

    @Override
    public List<Adicional> findDisponibles() {
        return adicionalRepository.findByDisponibleTrue();
    }

    @Override
    public List<Adicional> buscarDisponiblesPorCategoria(Long categoriaId) {
        return adicionalRepository.findByCategoriaIdAndDisponibleTrue(categoriaId);
    }

    @Override
    public boolean tienePedidos(Long id) {
        return adicionalRepository.existsEnDetallePedido(id);
    }

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
