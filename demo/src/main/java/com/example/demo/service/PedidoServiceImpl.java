package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Pedido;
import com.example.demo.repository.PedidoRepository;

@Service 
public class PedidoServiceImpl implements PedidoService {

    @Autowired 
    private PedidoRepository pedidoRepository;

	@Override
	public Collection<Pedido> findAll() {
		 return pedidoRepository.findAll();
	}

	@Override
	public Pedido findById(Long id) {
		return pedidoRepository.findById(id).orElse(null);
	}

	@Override
	public Pedido guardar(Pedido pedido) {
		return pedidoRepository.save(pedido);
	}

	@Override
	public void deleteById(Long id) {
		pedidoRepository.deleteById(id);
	}

    @Override
    public List<Pedido> findByClienteId(Long clienteId) {
        // Obtiene todos los pedidos que pertenecen al cliente con ese ID
        return pedidoRepository.findByClienteClienteId(clienteId);
    }
    
}
