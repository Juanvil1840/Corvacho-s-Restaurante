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

// Implementacion del servicio para la gestion de operadores
@Service
public class OperadorServiceImpl implements OperadorService {

    private final OperadorRepository operadorRepository;
    private final DomiciliarioRepository domiciliarioRepository;
    private final PedidoRepository pedidoRepository;

    // Constructor con inyeccion de repositorios
    public OperadorServiceImpl(OperadorRepository operadorRepository, DomiciliarioRepository domiciliarioRepository, PedidoRepository pedidoRepository) {
        this.operadorRepository = operadorRepository;
        this.domiciliarioRepository = domiciliarioRepository;
        this.pedidoRepository = pedidoRepository;
    }

    // Lista todos los operadores registrados
    @Override
    public Collection<Operador> findAll() {
        return operadorRepository.findAll();
    }

    // Busca un operador por su id o retorna null si no existe
    @Override
    public Operador findById(Long id) {
        return operadorRepository.findById(id).orElse(null);
    }

    // Guarda un operador en la base de datos
    @Override
    public Operador guardar(Operador operador) {
        return operadorRepository.save(operador);
    }

    // Valida los datos y registra un nuevo operador
    @Override
    public Operador crearOperador(Operador operador) {
        validarOperador(operador, null);
        operador.setId_operador(null);
        return operadorRepository.save(operador);
    }

    // Actualiza los datos de un operador existente
    @Override
    public Operador actualizarOperador(Long id, Operador datos) {
        Operador existente = findById(id);
        if (existente == null) {
            return null;
        }
        validarOperador(datos, id);
        existente.setNombre(datos.getNombre());
        existente.setUsuario(datos.getUsuario());
        // Solo cambia la contrasena si se envia una nueva no vacia
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            existente.setContrasena(datos.getContrasena());
        }
        return operadorRepository.save(existente);
    }

    // Elimina un operador desvinculando antes sus pedidos y domiciliarios asociados
    @Override
    @Transactional
    public boolean eliminarOperador(Long id) {
        Operador existente = findById(id);
        if (existente == null) {
            return false;
        }

        // Quita la referencia del operador en los pedidos que gestiono
        if (existente.getPedidos() != null) {
            for (Pedido pedido : existente.getPedidos()) {
                pedido.setOperador(null);
                pedidoRepository.save(pedido);
            }
            existente.getPedidos().clear();
        }

        // Quita la referencia del operador en los domiciliarios a su cargo
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

    // Elimina un operador directamente por su id
    @Override
    public void deleteById(Long id) {
        operadorRepository.deleteById(id);
    }

    // Comprueba si el usuario y contrasena coinciden con un operador registrado
    @Override
    public Operador autenticar(String usuario, String contrasena) {
        Operador operador = operadorRepository.findByUsuario(usuario);
        if (operador != null && operador.getContrasena().equals(contrasena)) {
            return operador;
        }
        return null;
    }

    // Valida las credenciales de login del operador o lanza excepcion si son invalidas
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

    // Valida campos obligatorios y que el nombre de usuario del operador sea unico
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
