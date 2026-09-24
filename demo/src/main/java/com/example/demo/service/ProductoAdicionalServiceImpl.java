package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.ProductoAdicional;
import com.example.demo.repository.ProductoAdicionalRepository;

@Service 
public class ProductoAdicionalServiceImpl implements ProductoAdicionalService {

    @Autowired 
    private ProductoAdicionalRepository productoAdicionalRepository;

	@Override
	public Collection<ProductoAdicional> findAll() {
		return productoAdicionalRepository.findAll();
	}

	@Override
	public ProductoAdicional findById(Long id) {
        return productoAdicionalRepository.findById(id).orElse(null);
	}

	@Override
	public ProductoAdicional guardar(ProductoAdicional productoAdicional) {
		return productoAdicionalRepository.save(productoAdicional);
	}

	@Override
	public void deleteById(Long id) {
		productoAdicionalRepository.deleteById(id);
	}

	@Override
	public List<ProductoAdicional> buscarPorCategoria(Long categoriaId) {
    	return productoAdicionalRepository.findByProductoCategoriaId(categoriaId);
	}
 
}
