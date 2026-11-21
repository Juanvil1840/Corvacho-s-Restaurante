package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.reportes.ReporteDashboardDTO;
import com.example.demo.entities.AdicionalSeleccionado;
import com.example.demo.entities.Categoria;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Pedido;
import com.example.demo.entities.Producto;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.PedidoRepository;

// Pruebas unitarias de los calculos del dashboard de reportes usando MOCKS (sin base de datos)
@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    // Periodo consultado: 3 dias (lunes 16 a miercoles 18 de noviembre de 2026)
    private static final LocalDate DESDE = LocalDate.of(2026, 11, 16);
    private static final LocalDate HASTA = LocalDate.of(2026, 11, 18);
    // Periodo anterior de la misma duracion: 13 a 15 de noviembre
    private static final LocalDate DESDE_ANTERIOR = LocalDate.of(2026, 11, 13);

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @Mock
    private ReporteExcelExporter excelExporter;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    private Producto lasagna;
    private Cliente ana;
    private Cliente luis;
    private long siguienteId = 1;

    @BeforeEach
    void setUp() {
        Categoria pastas = new Categoria("Pastas");
        pastas.setId(1L);
        lasagna = new Producto("Lasagna", 30000, "Pasta al horno", "lasagna.png", true, pastas);
        lasagna.setId(10L);

        ana = new Cliente("Ana", "Diaz", "ana@test.com", "pass", "300", "Calle 1", true);
        ana.setClienteId(1L);
        luis = new Cliente("Luis", "Rojas", "luis@test.com", "pass", "301", "Calle 2", true);
        luis.setClienteId(2L);
    }

    // 1. Calcula ventas, ticket, adicionales, tiempo de entrega y variacion frente al periodo anterior
    @Test
    void generarDashboard_calculaIndicadores() {
        // Periodo actual: 2 pedidos entregados
        Pedido p1 = pedido(ana, LocalDateTime.of(2026, 11, 16, 13, 0), 30);   // lunes 1pm
        Pedido p2 = pedido(luis, LocalDateTime.of(2026, 11, 18, 20, 0), 50);  // miercoles 8pm
        // 2 lasagnas con un adicional de 5.000 -> (30.000 + 5.000) x 2 = 70.000
        DetallePedido d1 = detalle(p1, 2, new AdicionalSeleccionado(20L, "Queso", 5000, 1));
        // 1 lasagna sin adicionales -> 30.000
        DetallePedido d2 = detalle(p2, 1);

        // Periodo anterior: 1 pedido de 50.000
        Pedido viejo = pedido(ana, LocalDateTime.of(2026, 11, 14, 12, 0), 40);
        DetallePedido dViejo = detalle(viejo, 1, new AdicionalSeleccionado(21L, "Pan", 20000, 1));

        simularPeriodo(DESDE, HASTA, List.of(p1, p2), List.of(d1, d2));
        simularPeriodo(DESDE_ANTERIOR, DESDE.minusDays(1), List.of(viejo), List.of(dViejo));
        when(pedidoRepository.findByEstadoNotIgnoreCase("Entregado")).thenReturn(List.of());

        ReporteDashboardDTO reporte = reporteService.generarDashboard(DESDE, HASTA);
        ReporteDashboardDTO.Indicadores ind = reporte.indicadores();

        assertThat(ind.ventas()).isEqualTo(100000);
        assertThat(ind.pedidos()).isEqualTo(2);
        assertThat(ind.ticketPromedio()).isEqualTo(50000);
        assertThat(ind.clientesUnicos()).isEqualTo(2);
        assertThat(ind.unidadesVendidas()).isEqualTo(3);
        assertThat(ind.ingresosAdicionales()).isEqualTo(10000);
        assertThat(ind.porcentajeAdicionales()).isCloseTo(10.0, within(0.001));
        assertThat(ind.minutosPromedioEntrega()).isCloseTo(40.0, within(0.001));
        // 100.000 frente a 50.000 del periodo anterior = +100%
        assertThat(ind.variacionVentas()).isCloseTo(100.0, within(0.001));

        // Un punto por dia del rango, incluido el martes sin ventas
        assertThat(reporte.ventasPorDia()).hasSize(3);
        assertThat(reporte.ventasPorDia().get(1).ventas()).isZero();

        // Mapa de calor: fila 0 = lunes, columna = hora
        assertThat(reporte.mapaCalor()[0][13]).isEqualTo(1);
        assertThat(reporte.mapaCalor()[2][20]).isEqualTo(1);

        assertThat(reporte.topProductos()).singleElement()
                .satisfies(r -> assertThat(r.cantidad()).isEqualTo(3));
    }

    // 2. Sin pedidos en el periodo anterior no hay con que comparar: la variacion es null (no infinito)
    @Test
    void generarDashboard_sinPeriodoAnterior_variacionNula() {
        Pedido p1 = pedido(ana, LocalDateTime.of(2026, 11, 17, 12, 0), 30);
        simularPeriodo(DESDE, HASTA, List.of(p1), List.of(detalle(p1, 1)));
        simularPeriodo(DESDE_ANTERIOR, DESDE.minusDays(1), List.of(), List.of());
        when(pedidoRepository.findByEstadoNotIgnoreCase("Entregado")).thenReturn(List.of());

        ReporteDashboardDTO reporte = reporteService.generarDashboard(DESDE, HASTA);

        assertThat(reporte.indicadores().variacionVentas()).isNull();
        assertThat(reporte.indicadores().variacionPedidos()).isNull();
    }

    // 3. Rango invalido (inicio despues del fin) responde 400 y no consulta la base de datos
    @Test
    void generarDashboard_rangoInvertido_lanza400() {
        assertThatThrownBy(() -> reporteService.generarDashboard(HASTA, DESDE))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));

        verify(pedidoRepository, never()).findCreadosEntre(any(), any());
    }

    // 4. Rangos de mas de un año se rechazan para no cargar demasiados datos
    @Test
    void generarDashboard_rangoMayorAUnAnio_lanza400() {
        assertThatThrownBy(() -> reporteService.generarDashboard(DESDE.minusYears(2), HASTA))
                .isInstanceOf(ResponseStatusException.class);
    }

    // ---------------------------------------------------------------- utilidades

    private void simularPeriodo(LocalDate desde, LocalDate hasta, List<Pedido> pedidos, List<DetallePedido> detalles) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();
        when(pedidoRepository.findCreadosEntre(eq(inicio), eq(fin))).thenReturn(pedidos);
        when(detallePedidoRepository.findDePedidosCreadosEntre(eq(inicio), eq(fin))).thenReturn(detalles);
    }

    private Pedido pedido(Cliente cliente, LocalDateTime creado, int minutosEntrega) {
        Pedido pedido = new Pedido("Entregado", creado, creado.plusMinutes(minutosEntrega), cliente, null, null);
        pedido.setId_Pedido(siguienteId++);
        return pedido;
    }

    private DetallePedido detalle(Pedido pedido, int cantidad, AdicionalSeleccionado... adicionales) {
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setProducto(lasagna);
        detalle.setCantidad(cantidad);
        detalle.setAdicionales(new ArrayList<>(List.of(adicionales)));
        return detalle;
    }
}
