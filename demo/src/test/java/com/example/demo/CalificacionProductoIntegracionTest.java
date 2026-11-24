package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Categoria;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Pedido;
import com.example.demo.entities.Producto;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.CalificacionProductoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CalificacionProductoIntegracionTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private DetallePedidoRepository detallePedidoRepository;
    @Autowired private CalificacionProductoRepository calificacionRepository;

    @Test
    @WithMockUser(username = "cliente-califica@test.com", authorities = "CLIENTE")
    void clienteCalificaProductoCompradoEnPedidoEntregado() throws Exception {
        var compra = crearCompra("cliente-califica@test.com", "Entregado", true);

        mockMvc.perform(post(url(compra)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pedidoId").value(compra.pedido().getId_Pedido()))
                .andExpect(jsonPath("$.productoId").value(compra.producto().getId()))
                .andExpect(jsonPath("$.puntuacion").value(5));
        mockMvc.perform(get("/api/pedidos/cliente/" + compra.cliente().getClienteId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productos[0].productoId").value(compra.producto().getId()));
        org.assertj.core.api.Assertions.assertThat(calificacionRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "cliente-ajeno@test.com", authorities = "CLIENTE")
    void clienteNoPuedeCalificarPedidoDeOtroCliente() throws Exception {
        var compra = crearCompra("propietario@test.com", "Entregado", true);
        mockMvc.perform(post(url(compra)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":4}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "cliente-califica@test.com", authorities = "CLIENTE")
    void soloPermiteCalificarPedidosEntregados() throws Exception {
        var compra = crearCompra("cliente-califica@test.com", "Cocinando", true);
        mockMvc.perform(post(url(compra)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":4}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "cliente-califica@test.com", authorities = "CLIENTE")
    void productoDebeEstarIncluidoEnElPedido() throws Exception {
        var compra = crearCompra("cliente-califica@test.com", "Entregado", false);
        mockMvc.perform(post(url(compra)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":4}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "cliente-califica@test.com", authorities = "CLIENTE")
    void evitaDuplicarProductoEnElMismoPedido() throws Exception {
        var compra = crearCompra("cliente-califica@test.com", "Entregado", true);
        mockMvc.perform(post(url(compra)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":4}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post(url(compra)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":5}"))
                .andExpect(status().isConflict());
        org.assertj.core.api.Assertions.assertThat(calificacionRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "cliente-califica@test.com", authorities = "CLIENTE")
    void puedeCalificarElMismoProductoEnOtroPedidoEntregado() throws Exception {
        var primeraCompra = crearCompra("cliente-califica@test.com", "Entregado", true);
        var segundaCompra = crearCompra("cliente-califica@test.com", "Entregado", true,
                primeraCompra.producto());
        mockMvc.perform(post(url(primeraCompra)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":4}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post(url(segundaCompra)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":5}"))
                .andExpect(status().isCreated());
        org.assertj.core.api.Assertions.assertThat(calificacionRepository.count()).isEqualTo(2);
    }

    @Test
    @WithMockUser(username = "cliente-califica@test.com", authorities = "CLIENTE")
    void rechazaPuntuacionesFueraDelRango() throws Exception {
        var compra = crearCompra("cliente-califica@test.com", "Entregado", true);
        mockMvc.perform(post(url(compra)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":0}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(url(compra)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":6}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "cliente-califica@test.com", authorities = "CLIENTE")
    void consultaSoloSusCalificaciones() throws Exception {
        var propia = crearCompra("cliente-califica@test.com", "Entregado", true);
        crearCompra("otro-cliente@test.com", "Entregado", true);
        mockMvc.perform(post(url(propia)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":3}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/calificaciones-productos/cliente/" + propia.cliente().getClienteId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pedidoId").value(propia.pedido().getId_Pedido()));
        mockMvc.perform(get("/api/calificaciones-productos/cliente/" +
                (propia.cliente().getClienteId() + 1)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "cliente-califica@test.com", authorities = "CLIENTE")
    void menuDevuelvePromedioYCantidadDeCalificaciones() throws Exception {
        var primeraCompra = crearCompra("cliente-califica@test.com", "Entregado", true);
        var segundaCompra = crearCompra("cliente-califica@test.com", "Entregado", true,
                primeraCompra.producto());
        mockMvc.perform(post(url(primeraCompra)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":5}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post(url(segundaCompra)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\":4}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/productos/" + primeraCompra.producto().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promedioCalificacion").value(4.5))
                .andExpect(jsonPath("$.cantidadCalificaciones").value(2));
    }

    private String url(Compra compra) {
        return "/api/calificaciones-productos/pedido/" + compra.pedido().getId_Pedido()
                + "/producto/" + compra.producto().getId();
    }

    private Compra crearCompra(String correo, String estado, boolean incluirProducto) {
        return crearCompra(correo, estado, incluirProducto, null);
    }

    private Compra crearCompra(String correo, String estado, boolean incluirProducto, Producto productoReutilizable) {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente == null) {
            cliente = clienteRepository.save(new Cliente("Cliente", "Prueba", correo, "clave123",
                    "3001234567", "Calle 1", true));
        }
        Categoria categoria = categoriaRepository.save(new Categoria("Categoria de prueba"));
        Producto producto = productoReutilizable != null ? productoReutilizable
                : productoRepository.save(new Producto("Plato de prueba", 20000, "Descripción", "plato.jpg", true,
                        categoria));
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEstado(estado);
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido = pedidoRepository.save(pedido);
        if (incluirProducto) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(1);
            detallePedidoRepository.save(detalle);
        }
        return new Compra(cliente, pedido, producto);
    }

    private record Compra(Cliente cliente, Pedido pedido, Producto producto) {}
}
