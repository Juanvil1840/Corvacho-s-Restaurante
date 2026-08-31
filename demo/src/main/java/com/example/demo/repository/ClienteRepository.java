
package com.example.demo.repository;


import com.example.demo.entities.Cliente;       // Entidad que representa a un cliente.
import java.util.Collection;                   // Interfaz para colecciones de objetos.
import java.util.HashMap;                      // Implementacion de mapa basada en hash.
import java.util.Map;                          // Interfaz para mapas (clave-valor).
import java.util.concurrent.atomic.AtomicInteger; // Generador de IDs atomicos (seguro para concurrencia).
import org.springframework.stereotype.Repository; // Anotacion para indicar que es un repositorio.


@Repository
public class ClienteRepository {
    // ATRIBUTOS
    
    private Map<Integer, Cliente> data = new HashMap<>();

    // Generador de IDs atomicos.
    // - Garantiza que los IDs sean unicos 
    // - Inicia en 1.
    private AtomicInteger idGenerator = new AtomicInteger(1);

   
    // CONSTRUCTOR

    // Se ejecuta cuando Spring crea una instancia de este repositorio.
    // Sirve para cargar datos iniciales de prueba.
    public ClienteRepository() {
  
        // - ID: 1
        // - Nombre: Dolly
        // - Apellido: Parton
        // - Correo: dp@gmail.com
        // - Contrasena: Jolene
        // - Telefono: 3157274433
        // - Direccion: 3146 Glencliff Rd. Nashville
        // - Activo: true (esta activo por defecto)
        data.put(1, new Cliente(1, "Dolly", "Parton", "dp@gmail.com", "Jolene",
                "3157274433", "3146 Glencliff Rd. Nashville", true));

        // Configura el siguiente ID disponible en 2.
        // Asi, cuando se cree un nuevo cliente, se le asignara el ID 2.
        idGenerator.set(2);
    }

  
    // METODOS DE LECTURA (READ)


    // Busca un cliente por su ID.
    // - Parametro: id (Integer) - El ID del cliente a buscar.
    // - Retorna: Cliente - El objeto Cliente encontrado, o null si no existe.
    public Cliente findById(Integer id) {
        return data.get(id);
    }

    // Retorna todos los clientes almacenados.
    // - Retorna: Collection<Cliente> - Una coleccion con todos los clientes.
    public Collection<Cliente> findAll() {
        return data.values();
    }


    // METODOS PARA CRUD (CREATE, UPDATE, DELETE)


    // Guarda un cliente (crea o actualiza).
    // - Si el cliente no tiene ID (es null o 0), se le asigna uno nuevo.
    // - Si el cliente ya tiene ID, se actualiza el existente.
    // - Parametro: cliente (Cliente) - El cliente a guardar.
    // - Retorna: Cliente - El cliente guardado (con su ID asignado).
    public Cliente save(Cliente cliente) {
        // Verifica si el cliente es nuevo (sin ID)
        if (cliente.getClienteId() == null || cliente.getClienteId() == 0) {
            // Asigna un ID nuevo usando el generador atomico
            cliente.setClienteId(idGenerator.getAndIncrement());
        }
        // Guarda o actualiza el cliente en el mapa
        // - Si el ID ya existe, lo sobreescribe (actualiza).
        // - Si el ID no existe, lo agrega (crea).
        data.put(cliente.getClienteId(), cliente);
        return cliente;
    }

    // Elimina un cliente por su ID.
    // - Parametro: id (Integer) - El ID del cliente a eliminar.
    public void deleteById(Integer id) {
        data.remove(id);
    }

    // Verifica si existe un cliente con el ID dado.
    // - Parametro: id (Integer) - El ID a verificar.
    // - Retorna: boolean - true si existe, false si no.
    public boolean existsById(Integer id) {
        return data.containsKey(id);
    }
}