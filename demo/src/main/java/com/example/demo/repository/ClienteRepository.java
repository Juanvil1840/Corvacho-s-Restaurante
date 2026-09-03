package com.example.demo.repository;


import com.example.demo.entities.Cliente;       
import java.util.Collection;                 
import java.util.HashMap;                      
import java.util.Map;                         
import java.util.concurrent.atomic.AtomicInteger; 
import org.springframework.stereotype.Repository;


@Repository //Es un repositorio
public class ClienteRepository {
    
    private Map<Integer, Cliente> data = new HashMap<>(); //Creamos una base de datos en memoria y guardamos los datos en un Map

    private AtomicInteger idGenerator = new AtomicInteger(1); //Generamos los id para los clientes, empezando en 1  y va aumentando

 
    public ClienteRepository() { //Constructor
  
        //Ejemplo de un usuario:
        // D: 1
        // Nombre: Dolly
        // Apellido: Parton
        // Correo: dp@gmail.com
        // Contrasena: Jolene
        // Telefono: 3157274433
        // Direccion: 3146 Glencliff Rd. Nashville
        // Activo: true (esta activo por defecto)
        data.put(1, new Cliente(1, "Dolly", "Parton", "dp@gmail.com", "Jolene",
                "3157274433", "3146 Glencliff Rd. Nashville", true));

        data.put(2, new Cliente(2, "Carlos", "Corvacho", "carlos@gmail.com", "1234",
                "3001234567", "Calle 40 #7-60 Bogotá", true));

        data.put(3, new Cliente(3, "María", "López", "maria@gmail.com", "1234",
                "3109876543", "Carrera 15 #82-10 Bogotá", true));

        data.put(4, new Cliente(4, "Andrés", "García", "andres@gmail.com", "1234",
                "3201234890", "Avenida 68 #45-20 Bogotá", true));

        data.put(5, new Cliente(5, "Valentina", "Rodríguez", "valentina@gmail.com", "1234",
                "3154567890", "Calle 100 #19-61 Bogotá", true));

        data.put(6, new Cliente(6, "Santiago", "Martínez", "santiago@gmail.com", "1234",
                "3187654321", "Carrera 7 #32-16 Bogotá", false));

    
       
        idGenerator.set(7); //Cuando se cree un nuevo cliente, se le asignara el ID 7.
    }

  
    public Cliente findById(Integer id) { //Buscamos a un cliente por su id y lo retornamos
        return data.get(id); //Le pasamos el id por parametro y devuelve toda la data del cliente con ese id
    }

  
    public Collection<Cliente> findAll() { //Metodo que devuelve todos los clientes
        return data.values(); //A diferencia de la anterior funcion este no tiene algun parametro de id porque necesitamos a todos, no a uno solo especifico
    }


    public Cliente findByCorreo(String correo) { //Buscar un cliente por su correo electronico
        for (Cliente cliente : data.values()) { //Obtenemos los datos de todos los clientes
            if (cliente.getCorreo().equals(correo)) { //Si un correo de un cliente equivale al objeto correo que pedimos lo retora
                return cliente; //Retornamos cliente
            }
        }
        return null; //Si no existe retornamos null
    }



    public Cliente save(Cliente cliente) { //Funcion para guardar un cliente 
       
        if (cliente.getClienteId() == null || cliente.getClienteId() == 0) {  // Verifica si el cliente es nuevo (sin ID)
            
            cliente.setClienteId(idGenerator.getAndIncrement()); //Asigna un ID nuevo usando 
        }
        data.put(cliente.getClienteId(), cliente); //Guarda el cliente en el mapa, si el cliente ya existe (por su ID) lo actualiza y si no lo crea
        return cliente; //Retornamos el objeto cliente
    }

    
    public void deleteById(Integer id) { // Elimina un cliente por su ID
        data.remove(id); 
    }


    public boolean existsById(Integer id) {// Verifica si existe un cliente con el ID dado, funcion de tipo bool
        return data.containsKey(id); //Si en el Map contiene al cliente con el id, este responde true o false
    }
}