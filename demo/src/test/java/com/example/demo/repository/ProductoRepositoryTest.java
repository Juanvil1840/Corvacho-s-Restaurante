package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entities.Categoria;
import com.example.demo.entities.Producto;

import static org.assertj.core.api.Assertions.assertThat;

// Pruebas para el CRUD del repositorio de Producto
@DataJpaTest
public class ProductoRepositoryTest {

    // Inyeccion de los repositorios necesarios
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Variables 
    private Producto productoPrueba;
    private Categoria categoriaPrueba;

    // Se ejecuta antes de cada prueba para tener datos base
    @BeforeEach
    public void setup() {
        // Creamos una categoria base para asociar el producto
        categoriaPrueba = categoriaRepository.save(new Categoria("Comidas Rapidas"));
        
        // Creamos el producto inicial (Salchipapa)
        productoPrueba = productoRepository.save(new Producto(
            "Salchipapa", 
            15000, 
            "Salchipapa con queso y tocineta", 
            "salchipapa.jpg", 
            true, 
            categoriaPrueba
        ));
    }

    // Prueba - Create (Perro de 12)
    @Test
    public void testGuardarProducto() {
        
        Producto nuevoProducto = new Producto(
            "Perro de 12", 
            12000, 
            "Perro caliente con queso, cebolla y salsas", 
            "perro.jpg", 
            true, 
            categoriaPrueba
        );

        
        Producto guardado = productoRepository.save(nuevoProducto);

        
        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Perro de 12");
    }

    // Prueba - Read (Listar todos)
    @Test
    public void testListarProductos() {
        
        List<Producto> productos = productoRepository.findAll();

        
        assertThat(productos).isNotEmpty();
        assertThat(productos.size()).isEqualTo(1); 
    }

    // Buscar por ID (Salchipapa)
    @Test
    public void testBuscarProductoPorId() {
       
        Optional<Producto> encontrado = productoRepository.findById(productoPrueba.getId());

        
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Salchipapa");
    }

    // Producto No encontrado
    @Test
    public void testBuscarProductoNoExistente() {
        
        Optional<Producto> encontrado = productoRepository.findById(-1L);

        
        assertThat(encontrado).isEmpty();
    }

    // Prueba - Update (Actualizar la Empanada de Carne)
    @Test
    public void testActualizarProducto() {
        // Arrange
        productoPrueba.setNombre("Empanada de Carne");
        productoPrueba.setPrecio(3500);

        
        Producto actualizado = productoRepository.save(productoPrueba);

       
        assertThat(actualizado).isNotNull();
        assertThat(actualizado.getNombre()).isEqualTo("Empanada de Carne");
        assertThat(actualizado.getPrecio()).isEqualTo(3500);
    }

    // Prueba Delete
    @Test
    public void testEliminarProducto() {
        
        productoRepository.delete(productoPrueba);

        
        Optional<Producto> eliminado = productoRepository.findById(productoPrueba.getId());
        assertThat(eliminado).isEmpty();
    }
}