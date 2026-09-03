package com.example.demo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Producto;
import com.example.demo.repository.ProductoRepository;

@Service //Servicio
public class ProductoServiceImpl implements ProductoService {

    @Autowired //Inyeccion de dependencias
    private ProductoRepository productoRepository; //Variable productoRepository de ProductoRepository para utilizarla en service

    
    @Override
    public Collection<Producto> obtenerTodos() { //Obtener todos los productos
        return productoRepository.findAll(); //Con productosRepository utilizamos el finAll para encontrarlos a todos
    }

    @Override
    public Producto obtenerPorId(Integer id) { //Obtenemos un producto por su id
        return productoRepository.findById(id); //Retornamos el producto, con ayuda del productoRepository pasando el id por parametro
    }
    
    @Override
    public Producto guardar(Producto producto) { //Guardar producto
        return productoRepository.save(producto); //Podemos guardar el nuevo producto o actualizar uno ya existente
    }

    @Override
    public void eliminar(Integer id) { //Eliminar producto
        productoRepository.deleteById(id); //Eliminamos el producto por su id
    }

    @Override
    public boolean existe(Integer id) { //Verificar si el producto ya existe, funcion booleana
        return productoRepository.existsById(id); //Retornamos si existe o no
    }
}