package com.example.demo.repository;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Producto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class ClienteRepository {
    private Map<Integer, Cliente> data = new HashMap<>();

    public ClienteRepository(){
        data.put(1, new Cliente(1, "Dolly", "Parton", "dp@gmail.com", "Jolene",
         "3157274433", " 3146 Glencliff Rd. Nashville"));
    }

    public Cliente findById(Integer id) {
    return data.get(id);
    }

    public Collection<Cliente> findAll() {
        return data.values();
    }

    
}
