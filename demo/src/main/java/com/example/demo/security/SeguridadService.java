package com.example.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Pedido;
import com.example.demo.entities.Role;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;

// Revisiones de "dueno del recurso" que se usan en @PreAuthorize de los controladores,
// por ejemplo: un cliente solo puede ver su propio perfil, carrito y pedidos
@Component("seguridad")
public class SeguridadService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public SeguridadService(ClienteRepository clienteRepository, PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    // true si el usuario autenticado tiene ese rol
    public boolean tieneRol(String rol) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rol));
    }

    public boolean esPersonal() {
        return tieneRol(Role.ADMIN) || tieneRol(Role.OPERADOR);
    }

    // true si el usuario autenticado es el cliente con ese id
    public boolean esElCliente(Long clienteId) {
        if (clienteId == null || !tieneRol(Role.CLIENTE)) {
            return false;
        }
        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        return cliente != null && cliente.getCorreo().equals(usuarioActual());
    }

    // true si el pedido pertenece al cliente autenticado
    public boolean esPedidoDelCliente(Long pedidoId) {
        if (pedidoId == null || !tieneRol(Role.CLIENTE)) {
            return false;
        }
        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        return pedido != null && pedido.getCliente() != null
                && pedido.getCliente().getCorreo().equals(usuarioActual());
    }

    // true si el pedido que se va a crear es para el cliente autenticado
    public boolean esPedidoParaElCliente(Pedido pedido) {
        return pedido != null && pedido.getCliente() != null && esElCliente(pedido.getCliente().getClienteId());
    }

    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
