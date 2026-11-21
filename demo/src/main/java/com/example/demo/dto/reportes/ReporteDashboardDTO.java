package com.example.demo.dto.reportes;

import java.time.LocalDate;
import java.util.List;

// Todo lo que muestra el dashboard de reportes del administrador para un rango de fechas.
// Son records: objetos inmutables que solo llevan datos (Jackson los convierte a JSON directamente)
public record ReporteDashboardDTO(
        Periodo periodo,
        Indicadores indicadores,
        List<VentaDia> ventasPorDia,
        List<Ranking> topProductos,
        List<Ranking> topAdicionales,
        List<Ranking> ventasPorCategoria,
        List<Ranking> topClientes,
        List<Conteo> pedidosPorEstado,
        int[][] mapaCalor) {

    // Rango consultado (las fechas se incluyen completas)
    public record Periodo(LocalDate desde, LocalDate hasta, long dias) {
    }

    // Numeros principales del periodo y su variacion (%) frente al periodo anterior de la misma duracion.
    // La variacion es null cuando el periodo anterior no tuvo datos para comparar
    public record Indicadores(
            double ventas,
            Double variacionVentas,
            long pedidos,
            Double variacionPedidos,
            double ticketPromedio,
            Double variacionTicket,
            long clientesUnicos,
            Double variacionClientes,
            long unidadesVendidas,
            double ingresosAdicionales,
            double porcentajeAdicionales,
            Double minutosPromedioEntrega,
            long pedidosActivos) {
    }

    // Ventas y numero de pedidos de un dia
    public record VentaDia(LocalDate fecha, double ventas, long pedidos) {
    }

    // Elemento de un ranking: producto, adicional, categoria o cliente
    public record Ranking(Long id, String nombre, String detalle, String imagen, long cantidad, double total) {
    }

    // Cantidad de pedidos por estado
    public record Conteo(String nombre, long cantidad) {
    }
}
