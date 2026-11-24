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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Pedido;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ResenaRepository;

@SpringBootTest
// Base en memoria y sin las reseñas de demostracion, para que cada prueba parta de cero
@ActiveProfiles("test")
@TestPropertySource(properties = "app.demo.resenas=false")
@AutoConfigureMockMvc
@Transactional
class ResenaIntegracionTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private ResenaRepository resenaRepository;

    @Test
    @WithMockUser(username = "resena@test.com", authorities = "CLIENTE")
    void clienteCalificaPedidoEntregadoYNoPuedeRepetir() throws Exception {
        Pedido pedido = crearPedido("resena@test.com", "Entregado");
        String body = "{\"puntuacion\":5,\"comentario\":\"Excelente servicio\"}";

        mockMvc.perform(post("/api/resenas/pedido/" + pedido.getId_Pedido())
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pedidoId").value(pedido.getId_Pedido()))
                .andExpect(jsonPath("$.puntuacion").value(5))
                .andExpect(jsonPath("$.comentario").value("Excelente servicio"));

        mockMvc.perform(post("/api/resenas/pedido/" + pedido.getId_Pedido())
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
        org.assertj.core.api.Assertions.assertThat(resenaRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "otro@test.com", authorities = "CLIENTE")
    void clienteNoPuedeCalificarPedidoAjeno() throws Exception {
        Pedido pedido = crearPedido("dueno@test.com", "Entregado");
        mockMvc.perform(post("/api/resenas/pedido/" + pedido.getId_Pedido())
                .contentType(MediaType.APPLICATION_JSON).content("{\"puntuacion\":4}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "resena@test.com", authorities = "CLIENTE")
    void soloPermitePedidosEntregados() throws Exception {
        Pedido pedido = crearPedido("resena@test.com", "Cocinando");
        mockMvc.perform(post("/api/resenas/pedido/" + pedido.getId_Pedido())
                .contentType(MediaType.APPLICATION_JSON).content("{\"puntuacion\":4}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "resena@test.com", authorities = "CLIENTE")
    void rechazaPuntuacionesFueraDelRango() throws Exception {
        Pedido pedido = crearPedido("resena@test.com", "Entregado");
        mockMvc.perform(post("/api/resenas/pedido/" + pedido.getId_Pedido())
                .contentType(MediaType.APPLICATION_JSON).content("{\"puntuacion\":6}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/resenas/pedido/" + pedido.getId_Pedido())
                .contentType(MediaType.APPLICATION_JSON).content("{\"puntuacion\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "resena@test.com", authorities = "CLIENTE")
    void clienteConsultaSusResenas() throws Exception {
        Pedido pedido = crearPedido("resena@test.com", "Entregado");
        mockMvc.perform(post("/api/resenas/pedido/" + pedido.getId_Pedido())
                .contentType(MediaType.APPLICATION_JSON).content("{\"puntuacion\":3}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/resenas/cliente/" + pedido.getCliente().getClienteId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pedidoId").value(pedido.getId_Pedido()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void administradorConsultaResenasConClienteYPedido() throws Exception {
        Pedido pedido = crearPedido("admin-view@test.com", "Entregado");
        var resena = new com.example.demo.entities.Resena();
        resena.setPedido(pedido);
        resena.setPuntuacion(4);
        resena.setComentario("Bien");
        resenaRepository.save(resena);

        mockMvc.perform(get("/api/resenas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pedidoId").value(pedido.getId_Pedido()))
                .andExpect(jsonPath("$[0].cliente").value("Cliente Prueba"))
                .andExpect(jsonPath("$[0].puntuacion").value(4));
    }

    private Pedido crearPedido(String correo, String estado) {
        Cliente cliente = new Cliente("Cliente", "Prueba", correo, "clave123", "3001234567", "Calle 1", true);
        cliente = clienteRepository.save(cliente);
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEstado(estado);
        pedido.setFechaCreacion(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }
}
