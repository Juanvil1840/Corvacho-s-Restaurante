package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Administrador;
import com.example.demo.entities.Operador;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.OperadorRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IntegracionRestH2Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OperadorRepository operadorRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Test
    void crudOperadorSeReflejaEnH2() throws Exception {
        long conteoInicial = operadorRepository.count();

        String nuevoJson = """
                {
                  "nombre": "Operador Prueba Integracion",
                  "usuario": "op_integracion_test",
                  "contrasena": "clave123"
                }
                """;

        mockMvc.perform(post("/api/operadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nuevoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Operador Prueba Integracion"))
                .andExpect(jsonPath("$.usuario").value("op_integracion_test"));

        assertThat(operadorRepository.count()).isEqualTo(conteoInicial + 1);

        Operador creado = operadorRepository.findAll().stream()
                .filter(op -> "op_integracion_test".equals(op.getUsuario()))
                .findFirst()
                .orElseThrow();

        String actualizarJson = """
                {
                  "nombre": "Operador Actualizado H2",
                  "usuario": "op_integracion_test",
                  "contrasena": "nuevaClave456"
                }
                """;

        mockMvc.perform(put("/api/operadores/" + creado.getId_operador())
                .contentType(MediaType.APPLICATION_JSON)
                .content(actualizarJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Operador Actualizado H2"));

        Operador enH2 = operadorRepository.findById(creado.getId_operador()).orElseThrow();
        assertThat(enH2.getNombre()).isEqualTo("Operador Actualizado H2");

        mockMvc.perform(delete("/api/operadores/" + creado.getId_operador()))
                .andExpect(status().isNoContent());

        assertThat(operadorRepository.findById(creado.getId_operador())).isEmpty();
    }

    @Test
    void crudAdministradorSeReflejaEnH2() throws Exception {
        long conteoInicial = administradorRepository.count();

        String nuevoJson = """
                {
                  "nombre": "Administrador Prueba H2",
                  "usuario": "admin_prueba_h2",
                  "contrasena": "clave123"
                }
                """;

        mockMvc.perform(post("/api/administradores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nuevoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Administrador Prueba H2"));

        assertThat(administradorRepository.count()).isEqualTo(conteoInicial + 1);

        Administrador creado = administradorRepository.findAll().stream()
                .filter(a -> "Administrador Prueba H2".equals(a.getNombre()))
                .findFirst()
                .orElseThrow();

        String actualizarJson = """
                {
                  "nombre": "Administrador Modificado H2",
                  "usuario": "admin_prueba_h2"
                }
                """;

        mockMvc.perform(put("/api/administradores/" + creado.getId_administrador())
                .contentType(MediaType.APPLICATION_JSON)
                .content(actualizarJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Administrador Modificado H2"));

        Administrador enH2 = administradorRepository.findById(creado.getId_administrador()).orElseThrow();
        assertThat(enH2.getNombre()).isEqualTo("Administrador Modificado H2");

        mockMvc.perform(delete("/api/administradores/" + creado.getId_administrador()))
                .andExpect(status().isNoContent());

        assertThat(administradorRepository.findById(creado.getId_administrador())).isEmpty();
    }

    @Test
    void endpointsRestDeIntegracionRespondenConDatosDeH2() throws Exception {
        mockMvc.perform(get("/api/operadores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id_operador").exists());

        mockMvc.perform(get("/api/administradores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id_administrador").exists());

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").exists());

        mockMvc.perform(get("/api/detalles-pedido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id_DetallePedido").exists());
    }
}

