package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Pedido;
import com.example.demo.service.ClienteService;
import com.example.demo.service.PedidoService;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    // Historial general de pedidos (todos los pedidos ordenados por fecha)
    @GetMapping
    public String listarTodos(Model model) {
        List<Pedido> pedidos = pedidoService.findAllOrdenadosPorFecha();
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("titulo", "Historial General de Pedidos");
        return "pedidos/lista";
    }

    // Ver detalle de un pedido específico con productos, adicionales e info del cliente
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido == null) {
            return "redirect:/pedidos";
        }
        model.addAttribute("pedido", pedido);
        return "pedidos/detalle";
    }

    // Pedidos de un cliente específico (historial del cliente)
    @GetMapping("/cliente/{clienteId}")
    public String pedidosPorCliente(@PathVariable Long clienteId, Model model) {
        Cliente cliente = clienteService.obtenerPorId(clienteId);
        if (cliente == null) {
            return "redirect:/pedidos";
        }
        List<Pedido> pedidos = pedidoService.findByClienteId(clienteId);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("cliente", cliente);
        model.addAttribute("titulo", "Pedidos de " + cliente.getNombre() + " " + cliente.getApellido());
        return "pedidos/lista";
    }
}
