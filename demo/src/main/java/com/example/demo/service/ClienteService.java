package com.example.demo.service;

import java.util.Collection;

import com.example.demo.dto.LoginClienteDTO;
import com.example.demo.entities.Cliente;

// Interfaz de servicio para la gestion y autenticacion de clientes
public interface ClienteService {

    // === MÉTODOS DE LECTURA ===
    // Obtiene todos los clientes registrados
    Collection<Cliente> obtenerTodos();
    // Busca un cliente por su id
    Cliente obtenerPorId(Long id);
    // Verifica si un cliente existe por su id
    boolean existe(Long id);

    // === CRUD Y LÓGICA DE NEGOCIO ===
    // Guarda directamente una entidad Cliente
    Cliente guardar(Cliente cliente);
    // Registra un nuevo cliente validando correo unico y campos obligatorios
    Cliente crearCliente(Cliente cliente);
    // Actualiza la informacion de un cliente existente
    Cliente actualizarCliente(Long id, Cliente datos);
    // Elimina un cliente por su id
    void eliminar(Long id);

    // Desactiva la cuenta de un cliente
    void desactivar(Long id);
    // Reactiva la cuenta de un cliente
    void reactivar(Long id);

    // Autentica las credenciales de inicio de sesion de un cliente
    Cliente autenticarCliente(LoginClienteDTO credenciales);
}
