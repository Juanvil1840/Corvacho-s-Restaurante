package com.example.demo.service;

import java.util.Collection;
import java.util.List; // 👈 AGREGAR ESTO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Operador;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.repository.PedidoRepository;

@Service
public class DomiciliarioServiceImpl implements DomiciliarioService {

    @Autowired
    private DomiciliarioRepository domiciliarioRepository;

    @Autowired
    private OperadorRepository operadorRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

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
        domiciliario.setActivo(true);
        domiciliario.setOperador(buscarOperador(domiciliario));
        return domiciliarioRepository.save(domiciliario);
    }

    @Override
    public Domiciliario actualizarDomiciliario(Long id, Domiciliario domiciliario) {
        if (findById(id) == null) {
            return null;
        }
        domiciliario.setId_domiciliario(id);
        if (domiciliario.isActivo()) {
            domiciliario.setOperador(buscarOperador(domiciliario));
        } else {
            domiciliario.setOperador(null);
            domiciliario.setInformaciondisp(false);
        }
        return domiciliarioRepository.save(domiciliario);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Domiciliario domiciliario = domiciliarioRepository.findById(id).orElse(null);
        if (domiciliario == null) {
            return;
        }

        if (domiciliario.getPedidos() != null) {
            domiciliario.getPedidos().forEach(pedido -> {
                pedido.setDomiciliario(null);
                pedidoRepository.save(pedido);
            });
        }

        domiciliario.setOperador(null);
        domiciliarioRepository.save(domiciliario);
        domiciliarioRepository.delete(domiciliario);
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