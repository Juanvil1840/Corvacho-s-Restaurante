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

    // 1. REGISTRO: Mostrar formulario para crear cuenta
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/registro";
    }

    // 2. REGISTRO: Guardar nuevo cliente
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente) {
        cliente.setActivo(true);
        clienteService.guardar(cliente);
        return "redirect:/perfil/ver/" + cliente.getClienteId();
    }

    // 3. VER PERFIL: Mostrar datos del cliente por ID
    @GetMapping("/ver/{id}")
    public String verPerfil(@PathVariable Integer id, Model model) {
        Cliente cliente = clienteService.obtenerPorId(id);
        if (cliente == null) {
            return "redirect:/perfil/registro";
        }
        model.addAttribute("cliente", cliente);
        return "clientes/perfil";
    }

    // 4. EDITAR PERFIL: Mostrar formulario para editar
    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Integer id, Model model) {
        Cliente cliente = clienteService.obtenerPorId(id);
        if (cliente == null) {
            return "redirect:/perfil/registro";
        }
        model.addAttribute("cliente", cliente);
        return "clientes/editar";
    }

    // 5. EDITAR PERFIL: Guardar cambios
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Cliente cliente) {
        // Si la contraseña viene vacía, mantener la existente
        if (cliente.getContraseña() == null || cliente.getContraseña().isEmpty()) {
            Cliente existente = clienteService.obtenerPorId(cliente.getClienteId());
            if (existente != null) {
                cliente.setContraseña(existente.getContraseña());
            }
        }
        clienteService.guardar(cliente);
        return "redirect:/perfil/ver/" + cliente.getClienteId();
    }

    // 6. DESACTIVAR CUENTA: El cliente desactiva su propio perfil
    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Integer id) {
        Cliente cliente = clienteService.obtenerPorId(id);
        if (cliente != null) {
            cliente.setActivo(false);
            clienteService.guardar(cliente);
        }
        return "redirect:/perfil/registro";
    }

    // 7. REACTIVAR CUENTA: El cliente activa su perfil nuevamente
    @GetMapping("/reactivar/{id}")
    public String reactivar(@PathVariable Integer id) {
        Cliente cliente = clienteService.obtenerPorId(id);
        if (cliente != null) {
            cliente.setActivo(true);
            clienteService.guardar(cliente);
        }
        return "redirect:/perfil/ver/" + id;
    }
}