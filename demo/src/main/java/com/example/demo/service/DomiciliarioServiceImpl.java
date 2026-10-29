package com.example.demo.service;

import java.util.Collection;
import java.util.List;

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

    private final DomiciliarioRepository domiciliarioRepository;
    private final OperadorRepository operadorRepository;
    private final PedidoRepository pedidoRepository;

    public DomiciliarioServiceImpl(DomiciliarioRepository domiciliarioRepository, OperadorRepository operadorRepository, PedidoRepository pedidoRepository) {
        this.domiciliarioRepository = domiciliarioRepository;
        this.operadorRepository = operadorRepository;
        this.pedidoRepository = pedidoRepository;
    }

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
        validarDatosUnicos(domiciliario, null);
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
        validarDatosUnicos(domiciliario, id);
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

    // La cedula y el celular no se pueden repetir entre domiciliarios
    private void validarDatosUnicos(Domiciliario domiciliario, Long idActual) {
        Domiciliario conMismaCedula = domiciliarioRepository.findByCedula(domiciliario.getCedula()).orElse(null);
        if (conMismaCedula != null && !conMismaCedula.getId_domiciliario().equals(idActual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un domiciliario con esa cedula");
        }
        Domiciliario conMismoCelular = domiciliarioRepository.findByCelular(domiciliario.getCelular()).orElse(null);
        if (conMismoCelular != null && !conMismoCelular.getId_domiciliario().equals(idActual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un domiciliario con ese celular");
        }
    }
}