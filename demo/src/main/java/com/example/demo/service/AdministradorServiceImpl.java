package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Administrador;
import com.example.demo.repository.AdministradorRepository;

@Service
public class AdministradorServiceImpl implements AdministradorService{

    @Autowired 
    private AdministradorRepository administradorRepository;

	@Override
	public Collection<Administrador> findAll() {
        return administradorRepository.findAll();
	}

	@Override
	public Administrador findById(Long id) {
		return administradorRepository.findById(id).orElse(null);
	}

	@Override
	public Administrador guardar(Administrador administrador) {
		return administradorRepository.save(administrador);
	}

	@Override
	public void deleteById(Long id) {
		administradorRepository.deleteById(id);
	}

    
}
