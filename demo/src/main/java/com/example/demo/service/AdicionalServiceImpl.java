package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Adicional;
import com.example.demo.repository.AdicionalRepository;

@Service
public class AdicionalServiceImpl implements AdicionalService {

    @Autowired
    private AdicionalRepository adicionalRepository;

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
	public void deleteById(Long id) {
        adicionalRepository.deleteById(id);
	}
    
}
