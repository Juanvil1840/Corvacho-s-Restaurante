package com.example.demo.controller;

import com.example.demo.entities.Cliente;
import com.example.demo.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/perfil")
public class ClientePerfilController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/registro";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente) {
        cliente.setActivo(true);
        clienteService.guardar(cliente);
        return "redirect:/perfil/ver/" + cliente.getClienteId();
    }

    @GetMapping("/ver/{id}")
    public String verPerfil(@PathVariable Long id, Model model) {
        // ✅ Eliminamos la validación manual. El servicio lanza la excepción si no existe.
        Cliente cliente = clienteService.obtenerPorId(id);
        model.addAttribute("cliente", cliente);
        return "clientes/perfil";
    }

    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Long id, Model model) {
        // ✅ Eliminamos la validación manual. El servicio lanza la excepción si no existe.
        Cliente cliente = clienteService.obtenerPorId(id);
        model.addAttribute("cliente", cliente);
        return "clientes/editar";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Cliente cliente) {
        if (cliente.getContraseña() == null || cliente.getContraseña().isEmpty()) {
            Cliente existente = clienteService.obtenerPorId(cliente.getClienteId());
            if (existente != null) {
                cliente.setContraseña(existente.getContraseña());
            }
        }
        clienteService.guardar(cliente);
        return "redirect:/perfil/ver/" + cliente.getClienteId();
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id) {
        Cliente cliente = clienteService.obtenerPorId(id);
        if (cliente != null) {
            cliente.setActivo(false);
            clienteService.guardar(cliente);
        }
        return "redirect:/perfil/registro";
    }

    @GetMapping("/reactivar/{id}")
    public String reactivar(@PathVariable Long id) {
        Cliente cliente = clienteService.obtenerPorId(id);
        if (cliente != null) {
            cliente.setActivo(true);
            clienteService.guardar(cliente);
        }
        return "redirect:/perfil/ver/" + id;
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return "redirect:/perfil/registro";
    }
}