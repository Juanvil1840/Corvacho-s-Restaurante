package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Operador;
import com.example.demo.repository.OperadorRepository;

@Service
public class OperadorServiceImpl implements OperadorService {

    @Autowired
    private OperadorRepository operadorRepository; 

	@Override
	public Collection<Operador> findAll() {
		return operadorRepository.findAll();
	}

	@Override
	public Operador findById(Long id) {
		return operadorRepository.findById(id).orElse(null);
	}

	@Override
	public Operador guardar(Operador operador) {
		return operadorRepository.save(operador);
	}

	@Override
	public void deleteById(Long id) {
		operadorRepository.deleteById(id);
	}

    
}
