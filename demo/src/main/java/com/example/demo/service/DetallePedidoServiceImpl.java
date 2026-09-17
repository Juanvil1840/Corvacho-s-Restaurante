package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.DetallePedido;
import com.example.demo.repository.DetallePedidoRepository;

@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Override
    public Collection<DetallePedido> findAll() {
        return detallePedidoRepository.findAll();
    }

    @Override
    public DetallePedido findById(Long id) {
        return detallePedidoRepository.findById(id).orElse(null);
    }

    @Override
    public DetallePedido guardar(DetallePedido detallepedido) {
        return detallePedidoRepository.save(detallepedido);
    }

    @Override
    public void deleteById(Long id) {
        detallePedidoRepository.deleteById(id);
    }

    
}
