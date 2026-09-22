package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/perfil") // Todas las url del controlador empiezan con perfil
public class ClientePerfilController {

    @Autowired // Inyeccion de dependencias de Spring
    private ClienteService clienteService; // Utilizamos esta variable para llamar a los metodos del ClienteService

    // Registro del usuario: Cuando el usuario entre a /registro se ejecuta este
    // metodo
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) { // Metodo que muestra eñ formulario de registro
        model.addAttribute("cliente", new Cliente()); // Agregamos un cliente vacio al modelo
        return "clientes/registro"; // Retornamos el registro.hml que esta dentro de clientes
    }

    // Guardar cliente: Cuando el usuario se registre y ya halla ingresado sus datos
    @PostMapping("/guardar") // Vamos a recibir los datos del formulario que el cliente envio
    public String guardar(@ModelAttribute Cliente cliente) { // Toma los datos y los convertimos en un objeto Cliente
        Cliente guardado = clienteService.registrar(cliente); // Le pedimos al service que active y guarde al cliente nuevo
        return "redirect:/perfil/ver/" + guardado.getClienteId(); // Rederigimos al cliente para que vea su perfil,
                                                                   // entonces si el nuevo cliente tiene un ID de 2, seria
                                                                   // /perfil/ver/2
    }

    // Ver perfil: Cuando queremos ver el perfil de un cliente con su id
    @GetMapping("/ver/{id}")
    public String verPerfil(@PathVariable Long id, Model model) { // PathVariable Long id nos permite que si la
                                                                     // URL es perfil/ver/5 se combierte a id=5
        Cliente cliente = clienteService.obtenerPorId(id); // Le pedimos al servicio a clienteService que busque al
                                                           // cliente con ese ID en el repositorio
        if (cliente == null) { // Si el cliente no existe o el ID no es valido, lo redirigimos a registro para
                               // evitar errores
            return "redirect:/perfil/registro"; // Lo llevamos a registro
        }
        model.addAttribute("cliente", cliente); // Guardamos el cliente en el modelo para que la vista pueda mostrar los
                                                // datos del cliente
        return "clientes/perfil"; // Mostramos el perfil
    }

    // Edigtar perfil: Cuando el cliente quiera cambiar algunos de sus datos
    @GetMapping("/editar/{id}")
    public String mostrarEditar(@PathVariable Long id, Model model) { // Toma el numero que viene con la URL
        Cliente cliente = clienteService.obtenerPorId(id); // Se busca a traves del service el cliente por su id
        if (cliente == null) { // Si el cliente no existe o el id es invalido lo redirigimos al registtro
            return "redirect:/perfil/registro";
        }
        model.addAttribute("cliente", cliente); // Guardamos el cliente en el modelo y se muestra el formulario para
                                                // editar sus datos
        return "clientes/editar";
    }

    // Guardar cammbios: Cuando el cliente guarde los cambios que hizo
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Cliente cliente) { // Toma los datos y los convierte en un objeto cliente
        Cliente actualizado = clienteService.actualizar(cliente); // Le pedimos al service que conserve la contraseña si no mandaron una nueva y guarde los cambios
        return "redirect:/perfil/ver/" + actualizado.getClienteId(); // Lo redirigimos a su perfil para que vea los cambios
    }

    // Desactivar cuenta: Le damos la opcion al cliente para que desactive su
    // cuenta, mas no la elimine
    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id) { // Toma el numero que viene con la URL
        clienteService.desactivar(id); // Le pedimos al service que busque al cliente y lo desactive
        return "redirect:/perfil/registro"; // Lo mandamos a la pagina de registro
    }

    // Reactivar la cuenta: Despues de desactivar la cuenta el cliente tiene la
    // opcion de volver a activarla si desea
    @GetMapping("/reactivar/{id}")
    public String reactivar(@PathVariable Long id) {// Toma el numero que viene con la URL
        clienteService.reactivar(id); // Le pedimos al service que busque al cliente y lo vuelva a activar
        return "redirect:/perfil/ver/" + id; // Lo redirigimos a su perfil para que lo pueda ver
    }

    // Elimina la cuenta(Borra la cuenta): Cuando el cliente quiere borrar su cuenta
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) { // Toma el numero que viene con la URL
        clienteService.eliminar(id); // Le pedimos al servicio que elimine la cuenta del cliente por su id
        return "redirect:/perfil/registro"; // Redirigimos al cliente a resgistro
    }
}