package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Pedido;
import com.example.demo.service.ClienteService;
import com.example.demo.service.PedidoService;

// Controlador para las vistas de Pedidos
@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;

    public PedidoController( PedidoService pedidoService, ClienteService clienteService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
    }

    // Muestra la lista de todos los pedidos registrados
    @GetMapping
    public String listarTodos(Model model) {
        model.addAttribute("pedidos", pedidoService.findAll());
        model.addAttribute("titulo", "Todos los Pedidos");
        return "pedidos/lista";
    }

    // Muestra los pedidos asociados a un cliente 
    @GetMapping("/cliente/{clienteId}")
    public String listarPorCliente(
            @PathVariable Long clienteId,
            Model model) {
        Cliente cliente = clienteService.obtenerPorId(clienteId);

        List<Pedido> pedidos = pedidoService.findByClienteId(clienteId);

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("cliente", cliente);
        model.addAttribute(
                "titulo",
                "Mis Pedidos - " + cliente.getNombre() + " " + cliente.getApellido());

        return "pedidos/lista";
    }

    // Muestra el detalle de un pedido por su id
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.findById(id);
        model.addAttribute("pedido", pedido);
        return "pedidos/detalle";
    }
}