package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Adicional;
import com.example.demo.entities.AdicionalSeleccionado;
import com.example.demo.entities.Categoria;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Domiciliario;
import com.example.demo.entities.Pedido;
import com.example.demo.entities.Producto;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.OperadorRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProductoRepository;

// Pruebas unitarias de PedidoService utilizando MOCKS con Mockito (sin base de datos real)
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private OperadorRepository operadorRepository;

    @Mock
    private DomiciliarioRepository domiciliarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @Mock
    private AdicionalRepository adicionalRepository;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Cliente clienteActivo;
    private Domiciliario domiciliarioLibre;
    private Producto productoValido;
    private Adicional adicionalValido;

    @BeforeEach
    void setUp() {
        clienteActivo = new Cliente("Juan", "Mendoza", "juan@test.com", "pass123", "3001234567", "Calle 1", true);
        clienteActivo.setClienteId(1L);

        domiciliarioLibre = new Domiciliario("Carlos Domiciliario", "3109998877", "CC-101", true, null);
        domiciliarioLibre.setId_domiciliario(2L);
        domiciliarioLibre.setInformaciondisp(true);

        Categoria categoria = new Categoria("Pastas");
        categoria.setId(1L);

        productoValido = new Producto("Lasagna", 30000, "Deliciosa pasta", "lasagna.png", true, categoria);
        productoValido.setId(10L);

        adicionalValido = new Adicional("Queso parmesano", 5000, true, categoria);
        adicionalValido.setId_adicional(20L);
    }

    // 1. Crear pedido exitoso: guarda el pedido en "Recibido" y sus detalles con datos reales de los mocks
    @Test
    void crearPedido_exitoso_guardaPedidoYDetalles() {
        Pedido pedido = crearPedidoBase(clienteActivo, domiciliarioLibre);
        AdicionalSeleccionado adicionalEnv = new AdicionalSeleccionado(20L, "Falso", 1, 1);
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setProducto(productoValido);
        detalle.setCantidad(2);
        detalle.setAdicionales(new ArrayList<>(List.of(adicionalEnv)));
        pedido.setDetallePedidos(new ArrayList<>(List.of(detalle)));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));
        when(domiciliarioRepository.findById(2L)).thenReturn(Optional.of(domiciliarioLibre));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId_Pedido(100L);
            return p;
        });
        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoValido));
        when(adicionalRepository.findById(20L)).thenReturn(Optional.of(adicionalValido));

        Pedido resultado = pedidoService.crearPedido(pedido);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId_Pedido()).isEqualTo(100L);
        assertThat(resultado.getEstado()).isEqualTo("Recibido");
        assertThat(resultado.getFechaCreacion()).isNotNull();

        verify(pedidoRepository).save(any(Pedido.class));
        verify(detallePedidoRepository).save(any(DetallePedido.class));
    }

    // 2. Crear pedido falla si el cliente está desactivado
    @Test
    void crearPedido_clienteDesactivado_lanzaExcepcionYNoGuarda() {
        clienteActivo.setActivo(false);
        Pedido pedido = crearPedidoBase(clienteActivo, null);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));

        assertThatThrownBy(() -> pedidoService.crearPedido(pedido))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST))
                .hasMessageContaining("desactivado");

        verify(pedidoRepository, never()).save(any(Pedido.class));
        verify(detallePedidoRepository, never()).save(any(DetallePedido.class));
    }

    // 3. Crear pedido falla si el producto está agotado
    @Test
    void crearPedido_productoAgotado_lanzaExcepcion() {
        productoValido.setDisponible(false);
        Pedido pedido = crearPedidoBase(clienteActivo, null);
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setProducto(productoValido);
        detalle.setCantidad(1);
        pedido.setDetallePedidos(new ArrayList<>(List.of(detalle)));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId_Pedido(100L);
            return p;
        });
        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoValido));

        assertThatThrownBy(() -> pedidoService.crearPedido(pedido))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("agotado");
    }

    // 4. Actualizar pedido a estado 'Enviado' cambia la disponibilidad del domiciliario a false (ocupado)
    @Test
    void actualizarPedido_aEnviado_ocupaAlDomiciliario() {
        Pedido existente = new Pedido();
        existente.setId_Pedido(50L);
        existente.setCliente(clienteActivo);
        existente.setDomiciliario(domiciliarioLibre);
        existente.setEstado("Cocinando");
        existente.setFechaCreacion(LocalDateTime.now());

        when(pedidoRepository.findById(50L)).thenReturn(Optional.of(existente));
        when(domiciliarioRepository.findById(2L)).thenReturn(Optional.of(domiciliarioLibre));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido cambio = new Pedido();
        cambio.setEstado("Enviado");
        cambio.setDomiciliario(domiciliarioLibre);

        Pedido resultado = pedidoService.actualizarPedido(50L, cambio);

        assertThat(resultado.getEstado()).isEqualTo("Enviado");
        assertThat(domiciliarioLibre.isInformaciondisp()).isFalse();
        verify(domiciliarioRepository).save(domiciliarioLibre);
        verify(pedidoRepository).save(existente);
    }

    // 5. Actualizar pedido rechaza saltos de estado no permitidos (ej. Recibido -> Entregado)
    @Test
    void actualizarPedido_transicionInvalida_lanzaExcepcion() {
        Pedido existente = new Pedido();
        existente.setId_Pedido(50L);
        existente.setCliente(clienteActivo);
        existente.setEstado("Recibido");
        existente.setFechaCreacion(LocalDateTime.now());

        when(pedidoRepository.findById(50L)).thenReturn(Optional.of(existente));

        Pedido cambioInvalido = new Pedido();
        cambioInvalido.setEstado("Entregado");

        assertThatThrownBy(() -> pedidoService.actualizarPedido(50L, cambioInvalido))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Transición no permitida");

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    // 6. Eliminar pedido enviado libera al domiciliario asociado
    @Test
    void deleteById_pedidoEnviado_liberaDomiciliarioYBorraPedido() {
        domiciliarioLibre.setInformaciondisp(false); // Estaba ocupado
        Pedido existente = new Pedido();
        existente.setId_Pedido(50L);
        existente.setCliente(clienteActivo);
        existente.setDomiciliario(domiciliarioLibre);
        existente.setEstado("Enviado");
        existente.setFechaCreacion(LocalDateTime.now());

        when(pedidoRepository.findById(50L)).thenReturn(Optional.of(existente));

        pedidoService.deleteById(50L);

        assertThat(domiciliarioLibre.isInformaciondisp()).isTrue();
        verify(domiciliarioRepository).save(domiciliarioLibre);
        verify(pedidoRepository).deleteById(50L);
    }

    // 7. Buscar por ID: retorna el pedido cuando existe y null cuando no
    @Test
    void findById_comportamientoCorrecto() {
        Pedido existente = new Pedido();
        existente.setId_Pedido(10L);
        existente.setCliente(clienteActivo);
        existente.setEstado("Recibido");
        existente.setFechaCreacion(LocalDateTime.now());

        when(pedidoRepository.findById(10L)).thenReturn(Optional.of(existente));
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThat(pedidoService.findById(10L)).isNotNull();
        assertThat(pedidoService.findById(999L)).isNull();
    }

    // 8. Listar pedidos activos filtra los entregados
    @Test
    void findActivos_llamaAlRepositorioCorrectamente() {
        Pedido pedidoActivo = new Pedido();
        pedidoActivo.setId_Pedido(1L);
        pedidoActivo.setCliente(clienteActivo);
        pedidoActivo.setEstado("Recibido");
        pedidoActivo.setFechaCreacion(LocalDateTime.now());

        when(pedidoRepository.findByEstadoNotIgnoreCase("Entregado")).thenReturn(List.of(pedidoActivo));

        Collection<Pedido> resultado = pedidoService.findActivos();

        assertThat(resultado).hasSize(1);
        verify(pedidoRepository).findByEstadoNotIgnoreCase("Entregado");
    }

    // Metodo auxiliar para construir un pedido basico
    private Pedido crearPedidoBase(Cliente cliente, Domiciliario domiciliario) {
        Pedido p = new Pedido();
        Cliente refC = new Cliente();
        refC.setClienteId(cliente.getClienteId());
        p.setCliente(refC);

        if (domiciliario != null) {
            Domiciliario refD = new Domiciliario();
            refD.setId_domiciliario(domiciliario.getId_domiciliario());
            p.setDomiciliario(refD);
        }
        return p;
    }
}
