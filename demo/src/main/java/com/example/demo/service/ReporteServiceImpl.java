package com.example.demo.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.reportes.ReporteDashboardDTO;
import com.example.demo.dto.reportes.ReporteDashboardDTO.Conteo;
import com.example.demo.dto.reportes.ReporteDashboardDTO.Indicadores;
import com.example.demo.dto.reportes.ReporteDashboardDTO.Periodo;
import com.example.demo.dto.reportes.ReporteDashboardDTO.Ranking;
import com.example.demo.dto.reportes.ReporteDashboardDTO.VentaDia;
import com.example.demo.entities.AdicionalSeleccionado;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.DetallePedido;
import com.example.demo.entities.Pedido;
import com.example.demo.entities.Producto;
import com.example.demo.repository.DetallePedidoRepository;
import com.example.demo.repository.PedidoRepository;

// Calcula los reportes de ventas a partir de los pedidos y sus detalles.
// Se hacen solo 2 consultas por periodo (pedidos y detalles con JOIN FETCH) y el resto se agrupa en memoria
@Service
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    // Estados del pedido en el orden en que avanzan
    private static final List<String> ESTADOS = List.of("Recibido", "Cocinando", "Enviado", "Entregado");
    private static final String ENTREGADO = "Entregado";

    // Cuantos elementos se muestran en cada ranking del dashboard
    private static final int TOP_DASHBOARD = 5;
    // Rango maximo que se puede consultar (un año)
    private static final long MAX_DIAS = 366;

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ReporteExcelExporter excelExporter;

    public ReporteServiceImpl(PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository,
            ReporteExcelExporter excelExporter) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.excelExporter = excelExporter;
    }

    @Override
    public ReporteDashboardDTO generarDashboard(LocalDate desde, LocalDate hasta) {
        return construirReporte(desde, hasta, TOP_DASHBOARD);
    }

    @Override
    public byte[] exportarExcel(LocalDate desde, LocalDate hasta) {
        // En el Excel van los rankings completos, no solo el top 5
        return excelExporter.generar(construirReporte(desde, hasta, Integer.MAX_VALUE));
    }

    private ReporteDashboardDTO construirReporte(LocalDate desde, LocalDate hasta, int limiteRanking) {
        validarRango(desde, hasta);
        long dias = ChronoUnit.DAYS.between(desde, hasta) + 1;

        DatosPeriodo actual = cargarPeriodo(desde, hasta);
        // Periodo anterior de la misma duracion, para comparar (ej: estos 30 dias vs los 30 anteriores)
        DatosPeriodo anterior = cargarPeriodo(desde.minusDays(dias), desde.minusDays(1));

        Indicadores indicadores = new Indicadores(
                actual.ventas(),
                variacion(actual.ventas(), anterior.ventas()),
                actual.numeroPedidos(),
                variacion(actual.numeroPedidos(), anterior.numeroPedidos()),
                actual.ticketPromedio(),
                variacion(actual.ticketPromedio(), anterior.ticketPromedio()),
                actual.clientesUnicos(),
                variacion(actual.clientesUnicos(), anterior.clientesUnicos()),
                actual.detalles().stream().mapToLong(this::cantidad).sum(),
                actual.ingresosAdicionales(),
                actual.ventas() > 0 ? actual.ingresosAdicionales() * 100 / actual.ventas() : 0,
                minutosPromedioEntrega(actual.pedidos()),
                pedidoRepository.findByEstadoNotIgnoreCase(ENTREGADO).size());

        return new ReporteDashboardDTO(
                new Periodo(desde, hasta, dias),
                indicadores,
                ventasPorDia(desde, hasta, actual),
                topProductos(actual.detalles(), limiteRanking),
                topAdicionales(actual.detalles(), limiteRanking),
                ventasPorCategoria(actual.detalles()),
                topClientes(actual, limiteRanking),
                pedidosPorEstado(actual.pedidos()),
                mapaCalor(actual.pedidos()));
    }

    // ---------------------------------------------------------------- carga de datos

    // Pedidos y detalles de un periodo, con el total de cada pedido ya calculado
    private record DatosPeriodo(List<Pedido> pedidos, List<DetallePedido> detalles, Map<Long, Double> totalPorPedido) {

        double ventas() {
            return totalPorPedido.values().stream().mapToDouble(Double::doubleValue).sum();
        }

        long numeroPedidos() {
            return pedidos.size();
        }

        double ticketPromedio() {
            return pedidos.isEmpty() ? 0 : ventas() / pedidos.size();
        }

        long clientesUnicos() {
            return pedidos.stream()
                    .map(Pedido::getCliente)
                    .filter(c -> c != null)
                    .map(Cliente::getClienteId)
                    .distinct()
                    .count();
        }

        double ingresosAdicionales() {
            return detalles.stream()
                    .mapToDouble(d -> extrasPorUnidad(d) * Math.max(d.getCantidad(), 1))
                    .sum();
        }
    }

    private DatosPeriodo cargarPeriodo(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();
        List<Pedido> pedidos = pedidoRepository.findCreadosEntre(inicio, fin);
        List<DetallePedido> detalles = detallePedidoRepository.findDePedidosCreadosEntre(inicio, fin);

        Map<Long, Double> totalPorPedido = new HashMap<>();
        for (Pedido pedido : pedidos) {
            totalPorPedido.put(pedido.getId_Pedido(), 0.0);
        }
        for (DetallePedido detalle : detalles) {
            totalPorPedido.merge(detalle.getPedido().getId_Pedido(), subtotal(detalle), Double::sum);
        }
        return new DatosPeriodo(pedidos, detalles, totalPorPedido);
    }

    // ---------------------------------------------------------------- calculos

    // Mismo calculo que el historial del cliente: (precio del plato + adicionales) x cantidad
    private static double subtotal(DetallePedido detalle) {
        double precioBase = detalle.getProducto() != null ? detalle.getProducto().getPrecio() : 0;
        return (precioBase + extrasPorUnidad(detalle)) * Math.max(detalle.getCantidad(), 1);
    }

    private static double extrasPorUnidad(DetallePedido detalle) {
        if (detalle.getAdicionales() == null) {
            return 0;
        }
        return detalle.getAdicionales().stream()
                .mapToDouble(a -> a.getPrecio() * Math.max(a.getCantidad(), 1))
                .sum();
    }

    private long cantidad(DetallePedido detalle) {
        return Math.max(detalle.getCantidad(), 1);
    }

    // Variacion porcentual frente al periodo anterior; null si no hay con que comparar
    private Double variacion(double actual, double anterior) {
        if (anterior == 0) {
            return null;
        }
        return (actual - anterior) * 100 / anterior;
    }

    // Promedio de minutos entre que se crea el pedido y se entrega (solo pedidos entregados)
    private Double minutosPromedioEntrega(List<Pedido> pedidos) {
        return pedidos.stream()
                .filter(p -> ENTREGADO.equalsIgnoreCase(p.getEstado()))
                .filter(p -> p.getFechaCreacion() != null && p.getFechaEntrega() != null)
                .mapToLong(p -> Duration.between(p.getFechaCreacion(), p.getFechaEntrega()).toMinutes())
                // Se descartan datos imposibles (negativos o de mas de un dia)
                .filter(minutos -> minutos > 0 && minutos < 24 * 60)
                .average()
                .stream().boxed().findFirst()
                .orElse(null);
    }

    private List<VentaDia> ventasPorDia(LocalDate desde, LocalDate hasta, DatosPeriodo datos) {
        // Se crean todos los dias del rango en 0, asi la grafica no tiene huecos
        Map<LocalDate, double[]> porDia = new LinkedHashMap<>();
        for (LocalDate dia = desde; !dia.isAfter(hasta); dia = dia.plusDays(1)) {
            porDia.put(dia, new double[2]);
        }
        for (Pedido pedido : datos.pedidos()) {
            double[] acumulado = porDia.get(pedido.getFechaCreacion().toLocalDate());
            if (acumulado != null) {
                acumulado[0] += datos.totalPorPedido().getOrDefault(pedido.getId_Pedido(), 0.0);
                acumulado[1]++;
            }
        }
        List<VentaDia> resultado = new ArrayList<>();
        porDia.forEach((dia, valores) -> resultado.add(new VentaDia(dia, valores[0], (long) valores[1])));
        return resultado;
    }

    // Platos mas vendidos (por unidades)
    private List<Ranking> topProductos(List<DetallePedido> detalles, int limite) {
        Map<Long, Acumulado> porProducto = new HashMap<>();
        for (DetallePedido detalle : detalles) {
            Producto producto = detalle.getProducto();
            porProducto.computeIfAbsent(producto.getId(), id -> new Acumulado(id, producto.getNombre(),
                    producto.getCategoria() != null ? producto.getCategoria().getNombre() : "", producto.getImagen()))
                    .sumar(cantidad(detalle), subtotal(detalle));
        }
        return ordenar(porProducto, limite, Comparator.comparingLong(Acumulado::getCantidad)
                .thenComparingDouble(Acumulado::getTotal).reversed());
    }

    // Adicionales que mas se venden (cuanto suman las ventas cruzadas)
    private List<Ranking> topAdicionales(List<DetallePedido> detalles, int limite) {
        Map<Long, Acumulado> porAdicional = new HashMap<>();
        for (DetallePedido detalle : detalles) {
            if (detalle.getAdicionales() == null) {
                continue;
            }
            for (AdicionalSeleccionado adicional : detalle.getAdicionales()) {
                long unidades = (long) Math.max(adicional.getCantidad(), 1) * cantidad(detalle);
                porAdicional.computeIfAbsent(adicional.getId_adicional(),
                        id -> new Acumulado(id, adicional.getNombre(), null, null))
                        .sumar(unidades, adicional.getPrecio() * unidades);
            }
        }
        return ordenar(porAdicional, limite, Comparator.comparingDouble(Acumulado::getTotal).reversed());
    }

    private List<Ranking> ventasPorCategoria(List<DetallePedido> detalles) {
        Map<Long, Acumulado> porCategoria = new HashMap<>();
        for (DetallePedido detalle : detalles) {
            var categoria = detalle.getProducto().getCategoria();
            if (categoria == null) {
                continue;
            }
            porCategoria.computeIfAbsent(categoria.getId(), id -> new Acumulado(id, categoria.getNombre(), null, null))
                    .sumar(cantidad(detalle), subtotal(detalle));
        }
        return ordenar(porCategoria, Integer.MAX_VALUE, Comparator.comparingDouble(Acumulado::getTotal).reversed());
    }

    // Clientes que mas compran en el periodo (para programas de fidelizacion)
    private List<Ranking> topClientes(DatosPeriodo datos, int limite) {
        Map<Long, Acumulado> porCliente = new HashMap<>();
        for (Pedido pedido : datos.pedidos()) {
            Cliente cliente = pedido.getCliente();
            if (cliente == null) {
                continue;
            }
            porCliente.computeIfAbsent(cliente.getClienteId(), id -> new Acumulado(id,
                    cliente.getNombre() + " " + cliente.getApellido(), cliente.getCorreo(), null))
                    .sumar(1, datos.totalPorPedido().getOrDefault(pedido.getId_Pedido(), 0.0));
        }
        return ordenar(porCliente, limite, Comparator.comparingDouble(Acumulado::getTotal).reversed());
    }

    private List<Conteo> pedidosPorEstado(List<Pedido> pedidos) {
        Map<String, Long> conteo = new LinkedHashMap<>();
        ESTADOS.forEach(estado -> conteo.put(estado, 0L));
        for (Pedido pedido : pedidos) {
            String estado = pedido.getEstado() != null ? pedido.getEstado() : "Sin estado";
            conteo.merge(estado, 1L, Long::sum);
        }
        return conteo.entrySet().stream().map(e -> new Conteo(e.getKey(), e.getValue())).toList();
    }

    // Matriz [dia de la semana][hora] con la cantidad de pedidos: lunes = 0 ... domingo = 6
    private int[][] mapaCalor(List<Pedido> pedidos) {
        int[][] mapa = new int[7][24];
        for (Pedido pedido : pedidos) {
            LocalDateTime fecha = pedido.getFechaCreacion();
            mapa[fecha.getDayOfWeek().getValue() - 1][fecha.getHour()]++;
        }
        return mapa;
    }

    // ---------------------------------------------------------------- utilidades

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar las fechas desde y hasta");
        }
        if (desde.isAfter(hasta)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha 'desde' no puede ser posterior a 'hasta'");
        }
        if (ChronoUnit.DAYS.between(desde, hasta) + 1 > MAX_DIAS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rango máximo para un reporte es de un año");
        }
    }

    private List<Ranking> ordenar(Map<Long, Acumulado> datos, int limite, Comparator<Acumulado> orden) {
        return datos.values().stream()
                .sorted(orden)
                .limit(limite)
                .map(Acumulado::aRanking)
                .toList();
    }

    // Acumulador mutable para agrupar cantidades y totales antes de convertirlos en Ranking
    private static final class Acumulado {
        private final Long id;
        private final String nombre;
        private final String detalle;
        private final String imagen;
        private long cantidad;
        private double total;

        Acumulado(Long id, String nombre, String detalle, String imagen) {
            this.id = id;
            this.nombre = nombre;
            this.detalle = detalle;
            this.imagen = imagen;
        }

        Acumulado sumar(long cantidad, double total) {
            this.cantidad += cantidad;
            this.total += total;
            return this;
        }

        long getCantidad() {
            return cantidad;
        }

        double getTotal() {
            return total;
        }

        Ranking aRanking() {
            return new Ranking(id, nombre, detalle, imagen, cantidad, total);
        }
    }
}
