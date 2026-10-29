package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entities.DetallePedido;
import com.example.demo.repository.DetallePedidoRepository;

// Implementacion del servicio para la gestion de detalles de un pedido
@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    // Constructor para inyectar el repositorio de detalles de pedido
    public DetallePedidoServiceImpl(DetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    // Lista todos los detalles de pedido registrados
    @Override
    public Collection<DetallePedido> findAll() {
        return detallePedidoRepository.findAll();
    }

    // Busca un detalle de pedido por su id o devuelve null si no existe
    @Override
    public DetallePedido findById(Long id) {
        return detallePedidoRepository.findById(id).orElse(null);
    }

    // Guarda un detalle de pedido en la base de datos
    @Override
    public DetallePedido guardar(DetallePedido detallepedido) {
        return detallePedidoRepository.save(detallepedido);
    }

    // Elimina un detalle de pedido por su id
    @Override
    public void deleteById(Long id) {
        detallePedidoRepository.deleteById(id);
    }

    // Busca las lineas de detalle que pertenecen a un pedido especifico
    @Override
    public List<DetallePedido> findByPedidoId(Long pedidoId) {
        return detallePedidoRepository.findByPedido_Id_Pedido(pedidoId);
    }
}
