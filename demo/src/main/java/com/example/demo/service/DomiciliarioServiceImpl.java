package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Domiciliario;
import com.example.demo.repository.DomiciliarioRepository;

@Service
public class DomiciliarioServiceImpl implements DomiciliarioService {

    @Autowired 
    private DomiciliarioRepository domiciliarioRepository;

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
    public void deleteById(Long id) {
        domiciliarioRepository.deleteById(id);
    }
    
}
