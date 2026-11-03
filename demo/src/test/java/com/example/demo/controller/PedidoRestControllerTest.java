package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Pedido;
import com.example.demo.service.PedidoService;

// Pruebas unitarias con MOCKS del controlador REST (PedidoRestController) usando MockMvc y @MockitoBean
@WebMvcTest(PedidoRestController.class)
class PedidoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Se simula (mockea) el servicio de negocio para aislar completamente la capa web del controlador
    @MockitoBean
    private PedidoService pedidoService;

    private Pedido pedidoPrueba;
    private Cliente clientePrueba;

    @BeforeEach
    void setUp() {
        clientePrueba = new Cliente("Juan", "Mendoza", "juan@test.com", "pass123", "3001234567", "Calle 1", true);
        clientePrueba.setClienteId(1L);

        Domiciliario domiciliarioPrueba = new Domiciliario("Carlos", "3109998877", "CC-101", true, null);
        domiciliarioPrueba.setId_domiciliario(2L);

        pedidoPrueba = new Pedido();
        pedidoPrueba.setId_Pedido(1L);
        pedidoPrueba.setEstado("Recibido");
        pedidoPrueba.setFechaCreacion(LocalDateTime.now());
        pedidoPrueba.setCliente(clientePrueba);
        pedidoPrueba.setDomiciliario(domiciliarioPrueba);
    }

    // 1. Consulta tipo GET (Colección general): listar pedidos
    @Test
    void listar_retornaListaDePedidosDTO() throws Exception {
        when(pedidoService.findAll()).thenReturn(List.of(pedidoPrueba));

        mockMvc.perform(get("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id_Pedido").value(1L))
                .andExpect(jsonPath("$[0].estado").value("Recibido"))
                .andExpect(jsonPath("$[0].clienteNombre").value("Juan Mendoza"));

        verify(pedidoService).findAll();
    }

    // 2. Consulta tipo GET (Filtrada por parámetro query): listar solo pedidos activos
    @Test
    void listar_conParametroActivos_retornaSoloPedidosActivos() throws Exception {
        when(pedidoService.findActivos()).thenReturn(List.of(pedidoPrueba));

        mockMvc.perform(get("/api/pedidos")
                .param("activos", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id_Pedido").value(1L));

        verify(pedidoService).findActivos();
    }

    // 3. Consulta tipo GET (Por ID existente y no existente)
    @Test
    void buscarPorId_existente_retorna200ConDetalleDelPedido() throws Exception {
        when(pedidoService.findById(1L)).thenReturn(pedidoPrueba);
        when(pedidoService.findDetallesByPedidoId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/pedidos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_Pedido").value(1L))
                .andExpect(jsonPath("$.estado").value("Recibido"))
                .andExpect(jsonPath("$.cliente.clienteId").value(1L));

        verify(pedidoService).findById(1L);
    }

    @Test
    void buscarPorId_noExistente_retorna404NotFound() throws Exception {
        when(pedidoService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/pedidos/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(pedidoService).findById(999L);
    }

    // 4. Consulta tipo GET (Específica por recurso anidado): historial de pedidos por cliente
    @Test
    void listarPorCliente_retornaHistorialDelCliente() throws Exception {
        when(pedidoService.findByClienteId(1L)).thenReturn(List.of(pedidoPrueba));
        when(pedidoService.findDetallesByPedidoId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/pedidos/cliente/{clienteId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id_Pedido").value(1L))
                .andExpect(jsonPath("$[0].estado").value("Recibido"));

        verify(pedidoService).findByClienteId(1L);
    }

    // 5. Consulta tipo POST: creación de un nuevo pedido
    @Test
    void crear_conDatosValidos_retorna201CreatedYPedidoDTO() throws Exception {
        when(pedidoService.crearPedido(any(Pedido.class))).thenReturn(pedidoPrueba);
        when(pedidoService.findDetallesByPedidoId(1L)).thenReturn(Collections.emptyList());

        String jsonBody = """
                {
                    "estado": "Recibido",
                    "cliente": { "clienteId": 1 }
                }
                """;

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_Pedido").value(1L))
                .andExpect(jsonPath("$.estado").value("Recibido"));

        verify(pedidoService).crearPedido(any(Pedido.class));
    }

    // 6. Consulta tipo PUT: actualización de estado de un pedido
    @Test
    void actualizar_existente_retorna200OkYPedidoActualizado() throws Exception {
        pedidoPrueba.setEstado("Cocinando");
        when(pedidoService.actualizarPedido(eq(1L), any(Pedido.class))).thenReturn(pedidoPrueba);
        when(pedidoService.findDetallesByPedidoId(1L)).thenReturn(Collections.emptyList());

        String jsonBody = """
                {
                    "estado": "Cocinando"
                }
                """;

        mockMvc.perform(put("/api/pedidos/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("Cocinando"));

        verify(pedidoService).actualizarPedido(eq(1L), any(Pedido.class));
    }

    // 7. Consulta tipo DELETE: eliminación de un pedido existente
    @Test
    void eliminar_existente_retorna204NoContent() throws Exception {
        when(pedidoService.findById(1L)).thenReturn(pedidoPrueba);
        doNothing().when(pedidoService).deleteById(1L);

        mockMvc.perform(delete("/api/pedidos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(pedidoService).findById(1L);
        verify(pedidoService).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_retorna404NotFound() throws Exception {
        when(pedidoService.findById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/pedidos/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(pedidoService).findById(999L);
    }
}

