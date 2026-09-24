package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Categoria;
import com.example.demo.entities.Producto;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.service.ProductoService;

import jakarta.persistence.EntityManager;

@SpringBootTest
class ProductoUpdatetest {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void actualizarDebeMantenerLaCategoriaExistente() {
        Categoria categoriaOriginal = categoriaRepository.save(new Categoria("Entradas"));

        Producto producto = productoRepository.save(
                new Producto("Bruschetta", 25000, "Descripcion inicial", "img.jpg", true, categoriaOriginal));

        Producto datosActualizados = new Producto();
        datosActualizados.setId(producto.getId());
        datosActualizados.setNombre("Bruschetta especial");
        datosActualizados.setPrecio(29000);
        datosActualizados.setDescripcion("Descripcion actualizada");
        datosActualizados.setImagen("img-actualizada.jpg");
        datosActualizados.setDisponible(true);
        datosActualizados.setCategoria(null);

        Producto actualizado = productoService.actualizar(datosActualizados);
        entityManager.flush();
        entityManager.clear();
        Producto recargado = productoRepository.findById(producto.getId()).orElseThrow();

        assertThat(actualizado).isNotNull();
        assertThat(actualizado.getCategoria()).isNotNull();
        assertThat(actualizado.getCategoria().getId()).isEqualTo(categoriaOriginal.getId());
        assertThat(recargado.getCategoria()).isNotNull();
        assertThat(recargado.getCategoria().getId()).isEqualTo(categoriaOriginal.getId());
    }
}
