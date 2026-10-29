package com.example.demo.service;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Operador;
import com.example.demo.entities.Pedido;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.repository.PedidoRepository;

@Service
public class OperadorServiceImpl implements OperadorService {

    private final OperadorRepository operadorRepository;
    private final DomiciliarioRepository domiciliarioRepository;
    private final PedidoRepository pedidoRepository;

    public OperadorServiceImpl(OperadorRepository operadorRepository, DomiciliarioRepository domiciliarioRepository, PedidoRepository pedidoRepository) {
        this.operadorRepository = operadorRepository;
        this.domiciliarioRepository = domiciliarioRepository;
        this.pedidoRepository = pedidoRepository;
    }

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
    public Operador crearOperador(Operador operador) {
        validarOperador(operador, null);
        operador.setId_operador(null);
        return operadorRepository.save(operador);
    }

    @Override
    public Operador actualizarOperador(Long id, Operador datos) {
        Operador existente = findById(id);
        if (existente == null) {
            return null;
        }
        validarOperador(datos, id);
        existente.setNombre(datos.getNombre());
        existente.setUsuario(datos.getUsuario());
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            existente.setContrasena(datos.getContrasena());
        }
        return operadorRepository.save(existente);
    }

    @Override
    @Transactional
    public boolean eliminarOperador(Long id) {
        Operador existente = findById(id);
        if (existente == null) {
            return false;
        }

        if (existente.getPedidos() != null) {
            for (Pedido pedido : existente.getPedidos()) {
                pedido.setOperador(null);
                pedidoRepository.save(pedido);
            }
            existente.getPedidos().clear();
        }

        if (existente.getDomiciliarios() != null) {
            for (Domiciliario domiciliario : existente.getDomiciliarios()) {
                domiciliario.setOperador(null);
                domiciliarioRepository.save(domiciliario);
            }
            existente.getDomiciliarios().clear();
        }

        operadorRepository.deleteById(id);
        return true;
    }

    @Override
    public void deleteById(Long id) {
        operadorRepository.deleteById(id);
    }

    @Override
    public Operador autenticar(String usuario, String contrasena) {
        Operador operador = operadorRepository.findByUsuario(usuario);
        if (operador != null && operador.getContrasena().equals(contrasena)) {
            return operador;
        }
        return null;
    }

    @Override
    public Operador autenticarOperador(LoginRequestDTO credenciales) {
        if (credenciales == null || credenciales.getUsuario() == null || credenciales.getContrasena() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario y contraseña son obligatorios.");
        }
        Operador operador = autenticar(credenciales.getUsuario().trim(), credenciales.getContrasena());
        if (operador == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        }
        return operador;
    }

    private void validarOperador(Operador operador, Long idActual) {
        if (operador.getNombre() == null || operador.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del operador es obligatorio");
        }
        if (operador.getUsuario() == null || operador.getUsuario().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario del operador es obligatorio");
        }
        if (idActual == null && (operador.getContrasena() == null || operador.getContrasena().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña del operador es obligatoria");
        }

        Operador conMismoUsuario = operadorRepository.findByUsuario(operador.getUsuario().trim());
        if (conMismoUsuario != null && !conMismoUsuario.getId_operador().equals(idActual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está registrado");
        }
    }
}
