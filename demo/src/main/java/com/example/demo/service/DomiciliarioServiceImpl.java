package com.example.demo.service;

import java.util.Collection;
import java.util.List; // 👈 AGREGAR ESTO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Operador;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.OperadorRepository;

@Service
public class DomiciliarioServiceImpl implements DomiciliarioService {

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    @Autowired
    private OperadorRepository operadorRepository;

    @Override
    public Collection<Domiciliario> findAll() {
        return domiciliarioRepository.findAll();
    }

    @Override
    public Domiciliario findById(Long id) {
        return domiciliarioRepository.findById(id).orElse(null);
    }

    @Override
    public Domiciliario guardar(Domiciliario domiciliario) {
        return domiciliarioRepository.save(domiciliario);
    }

    @Override
    public Domiciliario crearDomiciliario(Domiciliario domiciliario) {
        domiciliario.setId_domiciliario(null);
        domiciliario.setOperador(buscarOperador(domiciliario));
        return domiciliarioRepository.save(domiciliario);
    }

    @Override
    public Domiciliario actualizarDomiciliario(Long id, Domiciliario domiciliario) {
        if (findById(id) == null) {
            return null;
        }
        domiciliario.setId_domiciliario(id);
        domiciliario.setOperador(buscarOperador(domiciliario));
        return domiciliarioRepository.save(domiciliario);
    }

    @Override
    public void deleteById(Long id) {
        domiciliarioRepository.deleteById(id);
    }

    @Override
    public List<Domiciliario> obtenerDisponibles() {
        return domiciliarioRepository.findByInformaciondispTrue();
    }

    private Operador buscarOperador(Domiciliario domiciliario) {
        if (domiciliario.getOperador() == null || domiciliario.getOperador().getId_operador() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un operador");
        }
        return operadorRepository.findById(domiciliario.getOperador().getId_operador())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El operador seleccionado no existe"));
    }
}