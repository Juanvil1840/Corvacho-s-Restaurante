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

    // Muestra TODOS los pedidos (vista general/admin)
    @GetMapping
    public String listarTodos(Model model) {
        model.addAttribute("pedidos", pedidoService.findAll());
        model.addAttribute("titulo", "Todos los Pedidos");
        return "pedidos/lista";
    }

    // Muestra los pedidos de un cliente específico
    // URL: /pedidos/cliente/5  → pedidos del cliente con ID 5
    @GetMapping("/cliente/{clienteId}")
    public String listarPorCliente(@PathVariable Long clienteId, Model model) {
        Cliente cliente = clienteService.obtenerPorId(clienteId);

        if (cliente == null) {
            // Si el cliente no existe, redirigimos al registro
            return "redirect:/perfil/registro";
        }

        // Obtiene solo los pedidos de ese cliente
        List<Pedido> pedidos = pedidoService.findByClienteId(clienteId);

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("cliente", cliente);
        model.addAttribute("titulo", "Mis Pedidos — " + cliente.getNombre() + " " + cliente.getApellido());
        return "pedidos/lista";
    }

    // Muestra el detalle de un pedido por su ID
    // URL: /pedidos/3  → detalle del pedido #3
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.findById(id);

        if (pedido == null) {
            // Si el pedido no existe, volvemos a la lista general
            return "redirect:/pedidos";
        }

        model.addAttribute("pedido", pedido);
        return "pedidos/detalle";
    }
}
