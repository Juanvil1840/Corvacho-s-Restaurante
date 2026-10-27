package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entities.Cliente;
import com.example.demo.service.ClienteService;

// Controlador para el perfil de los clientes
@Controller
@RequestMapping("/perfil")
public class ClientePerfilController {

    private final ClienteService clienteService;

    public ClientePerfilController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Muestra el formulario de registro de un nuevo cliente
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/registro";
    }

    // Guarda un nuevo cliente y redirige a su perfil
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente) {
        Cliente guardado = clienteService.registrar(cliente);
        return "redirect:/perfil/ver/" + guardado.getClienteId();
    }

    // Muestra el perfil de un cliente especifico
    @GetMapping("/ver/{id}")
    public String verPerfil(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.obtenerPorId(id);

        if (cliente == null) {
            return "redirect:/perfil/registro";
        }

        model.addAttribute("cliente", cliente);
        return "clientes/perfil";
    }

    // Muestra el formulario para editar un cliente
    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.obtenerPorId(id);

        if (cliente == null) {
            return "redirect:/perfil/registro";
        }

        model.addAttribute("cliente", cliente);
        return "clientes/editar";
    }

    // Actualiza los datos de un cliente existente
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Cliente cliente) {
        Cliente actualizado = clienteService.actualizar(cliente);
        return "redirect:/perfil/ver/" + actualizado.getClienteId();
    }

    // Desactiva la cuenta de un cliente
    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id) {
        clienteService.desactivar(id);
        return "redirect:/perfil/registro";
    }

    // Reactiva la cuenta de un cliente
    @GetMapping("/reactivar/{id}")
    public String reactivar(@PathVariable Long id) {
        clienteService.reactivar(id);
        return "redirect:/perfil/ver/" + id;
    }

    // Elimina la cuenta de un cliente de la base de datos
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return "redirect:/perfil/registro";
    }
}