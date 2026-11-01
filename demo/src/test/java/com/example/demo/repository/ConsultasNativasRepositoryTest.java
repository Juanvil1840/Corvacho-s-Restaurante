package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entities.Adicional;
import com.example.demo.entities.Categoria;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Pedido;
import com.example.demo.entities.Producto;

@DataJpaTest
class ConsultasNativasRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Test
    void findDisponiblesNative_filtraDisponibilidadYOrdenaPorId() {
        // Arrange
        Categoria categoria = categoriaRepository.save(new Categoria("Entradas"));
        Producto productoPosterior = productoRepository.save(producto("Disponible posterior", true, categoria));
        productoRepository.save(producto("No disponible", false, categoria));
        Producto productoAnterior = productoRepository.save(producto("Disponible anterior", true, categoria));

        // Act
        List<Producto> resultado = productoRepository.findDisponiblesNative();

        // Assert
        assertThat(resultado).extracting(Producto::getId)
                .containsExactly(productoPosterior.getId(), productoAnterior.getId());
        assertThat(resultado).extracting(Producto::getNombre)
                .doesNotContain("No disponible");
    }

    @Test
    void findDisponiblesByCategoriaNative_filtraCategoriaYDisponibilidad() {
        // Arrange
        Categoria categoriaObjetivo = categoriaRepository.save(new Categoria("Pastas"));
        Categoria otraCategoria = categoriaRepository.save(new Categoria("Bebidas"));
        Producto esperado = productoRepository.save(producto("Pasta disponible", true, categoriaObjetivo));
        productoRepository.save(producto("Pasta agotada", false, categoriaObjetivo));
        productoRepository.save(producto("Bebida disponible", true, otraCategoria));

        // Act
        List<Producto> resultado = productoRepository.findDisponiblesByCategoriaNative(categoriaObjetivo.getId());
        List<Producto> resultadoSinCoincidencias = productoRepository
                .findDisponiblesByCategoriaNative(Long.MAX_VALUE);

        // Assert
        assertThat(resultado).extracting(Producto::getId).containsExactly(esperado.getId());
        assertThat(resultadoSinCoincidencias).isEmpty();
    }

    @Test
    void findHistorialByClienteNative_filtraClienteYOrdenaDeMasRecienteAMasAntiguo() {
        // Arrange
        Cliente cliente = clienteRepository.save(cliente("historial@example.test"));
        Cliente otroCliente = clienteRepository.save(cliente("otro@example.test"));
        LocalDateTime ahora = LocalDateTime.of(2026, 3, 15, 12, 0);
        Pedido antiguo = pedidoRepository.save(pedido(cliente, "Recibido", ahora.minusDays(2)));
        Pedido ajeno = pedidoRepository.save(pedido(otroCliente, "Entregado", ahora));
        Pedido reciente = pedidoRepository.save(pedido(cliente, "Entregado", ahora.minusDays(1)));

        // Act
        List<Pedido> resultado = pedidoRepository.findHistorialByClienteNative(cliente.getClienteId());
        List<Pedido> resultadoSinCoincidencias = pedidoRepository
                .findHistorialByClienteNative(Long.MAX_VALUE);

        // Assert
        assertThat(resultado).extracting(Pedido::getId_Pedido)
                .containsExactly(reciente.getId_Pedido(), antiguo.getId_Pedido())
                .doesNotContain(ajeno.getId_Pedido());
        assertThat(resultadoSinCoincidencias).isEmpty();
    }

    @Test
    void findByEstadoNative_filtraEstadoSinDistinguirMayusculasYOrdenaPorFecha() {
        // Arrange
        Cliente cliente = clienteRepository.save(cliente("estado@example.test"));
        LocalDateTime ahora = LocalDateTime.of(2026, 4, 20, 18, 0);
        Pedido pendienteAntiguo = pedidoRepository.save(pedido(cliente, "Pendiente", ahora.minusDays(2)));
        pedidoRepository.save(pedido(cliente, "Entregado", ahora));
        Pedido pendienteReciente = pedidoRepository.save(pedido(cliente, "Pendiente", ahora.minusDays(1)));

        // Act
        List<Pedido> resultado = pedidoRepository.findByEstadoNative("pEnDiEnTe");
        List<Pedido> resultadoSinCoincidencias = pedidoRepository.findByEstadoNative("Cancelado");

        // Assert
        assertThat(resultado).extracting(Pedido::getId_Pedido)
                .containsExactly(pendienteReciente.getId_Pedido(), pendienteAntiguo.getId_Pedido());
        assertThat(resultadoSinCoincidencias).isEmpty();
    }

    @Test
    void findDisponiblesByCategoriaNative_enAdicionalesFiltraCategoriaYDisponibilidad() {
        // Arrange
        Categoria categoriaObjetivo = categoriaRepository.save(new Categoria("Acompanamientos"));
        Categoria otraCategoria = categoriaRepository.save(new Categoria("Bebidas"));
        Adicional esperado = adicionalRepository.save(adicional("Salsa", true, categoriaObjetivo));
        adicionalRepository.save(adicional("Extra agotado", false, categoriaObjetivo));
        adicionalRepository.save(adicional("Hielo", true, otraCategoria));

        // Act
        List<Adicional> resultado = adicionalRepository
                .findDisponiblesByCategoriaNative(categoriaObjetivo.getId());
        List<Adicional> resultadoSinCoincidencias = adicionalRepository
                .findDisponiblesByCategoriaNative(Long.MAX_VALUE);

        // Assert
        assertThat(resultado).extracting(Adicional::getId_adicional).containsExactly(esperado.getId_adicional());
        assertThat(resultadoSinCoincidencias).isEmpty();
    }

    private Producto producto(String nombre, boolean disponible, Categoria categoria) {
        return new Producto(nombre, 12000, "Producto de prueba", "producto-test.jpg", disponible, categoria);
    }

    private Cliente cliente(String correo) {
        return new Cliente("Cliente", "Prueba", correo, "clave-test", "3000000000", "Direccion de prueba", true);
    }

    private Pedido pedido(Cliente cliente, String estado, LocalDateTime fecha) {
        return new Pedido(estado, fecha, null, cliente, null, null);
    }

    private Adicional adicional(String nombre, boolean disponible, Categoria categoria) {
        return new Adicional(nombre, 1500, disponible, categoria);
    }
}
