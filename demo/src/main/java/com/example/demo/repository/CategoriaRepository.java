package com.example.demo.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.example.demo.entities.Categoria;

@Repository
public class CategoriaRepository{

    private Map<Integer, Categoria> data = new HashMap<>();

    public CategoriaRepository(){
        data.put(1, new Categoria(1, "Entradas"));
        data.put(2, new Categoria(2, "Pastas"));
        data.put(3, new Categoria(3, "Pescados y Mariscos"));
        data.put(4, new Categoria(4, "Carnes"));
        data.put(5, new Categoria(5, "Ensaladas"));
        data.put(6, new Categoria(6, "Pizzas"));
    }

    public Categoria findById(Integer id){
        return data.get(id);
    }

    public Collection<Categoria> findAll(){
        return data.values();
    }

}


