package com.example.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.CrearResenaDTO;
import com.example.demo.entities.Resena;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ResenaRepository;

@Service
public class ResenaService {
    private final ResenaRepository resenaRepository;
    private final PedidoRepository pedidoRepository;

    public ResenaService(ResenaRepository resenaRepository, PedidoRepository pedidoRepository) {
        this.resenaRepository = resenaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public Resena crear(Long pedidoId, CrearResenaDTO solicitud) {
        if (resenaRepository.existsByPedidoId(pedidoId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este pedido ya tiene una reseña");
        }
        var pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el pedido"));
        if (!"Entregado".equalsIgnoreCase(pedido.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo puedes calificar pedidos entregados");
        }
        var resena = new Resena();
        resena.setPedido(pedido);
        resena.setPuntuacion(solicitud.puntuacion());
        resena.setComentario(solicitud.comentario() == null || solicitud.comentario().isBlank()
                ? null : solicitud.comentario().trim());
        try {
            return resenaRepository.saveAndFlush(resena);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este pedido ya tiene una reseña");
        }
    }

    @Transactional(readOnly = true)
    public List<Resena> porCliente(Long clienteId) {
        return resenaRepository.findByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Resena> todas() {
        return resenaRepository.findAllForAdmin();
    }
}
