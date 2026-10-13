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

@Controller
@RequestMapping("/perfil")
public class ClientePerfilController {

    private final ClienteService clienteService;

    public ClientePerfilController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/registro";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente) {
        Cliente guardado = clienteService.registrar(cliente);
        return "redirect:/perfil/ver/" + guardado.getClienteId();
    }

    @GetMapping("/ver/{id}")
    public String verPerfil(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.obtenerPorId(id);

        if (cliente == null) {
            return "redirect:/perfil/registro";
        }

        model.addAttribute("cliente", cliente);
        return "clientes/perfil";
    }

    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.obtenerPorId(id);

        if (cliente == null) {
            return "redirect:/perfil/registro";
        }

        model.addAttribute("cliente", cliente);
        return "clientes/editar";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Cliente cliente) {
        Cliente actualizado = clienteService.actualizar(cliente);
        return "redirect:/perfil/ver/" + actualizado.getClienteId();
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id) {
        clienteService.desactivar(id);
        return "redirect:/perfil/registro";
    }

    @GetMapping("/reactivar/{id}")
    public String reactivar(@PathVariable Long id) {
        clienteService.reactivar(id);
        return "redirect:/perfil/ver/" + id;
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return "redirect:/perfil/registro";
    }
}
