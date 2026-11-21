package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.example.demo.dto.reportes.ReporteDashboardDTO;
import com.example.demo.dto.reportes.ReporteDashboardDTO.Indicadores;
import com.example.demo.dto.reportes.ReporteDashboardDTO.Ranking;
import com.example.demo.dto.reportes.ReporteDashboardDTO.VentaDia;

// Convierte un reporte en un archivo Excel (.xlsx) con Apache POI: una hoja por cada seccion
@Component
public class ReporteExcelExporter {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generar(ReporteDashboardDTO reporte) {
        try (Workbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Estilos estilos = new Estilos(libro);

            hojaResumen(libro, estilos, reporte);
            hojaVentasPorDia(libro, estilos, reporte.ventasPorDia());
            hojaRanking(libro, estilos, "Platos", "Plato", "Categoría", reporte.topProductos());
            hojaRanking(libro, estilos, "Adicionales", "Adicional", null, reporte.topAdicionales());
            hojaRanking(libro, estilos, "Categorías", "Categoría", null, reporte.ventasPorCategoria());
            hojaRanking(libro, estilos, "Clientes", "Cliente", "Correo", reporte.topClientes());

            libro.write(salida);
            return salida.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar el archivo Excel", e);
        }
    }

    private void hojaResumen(Workbook libro, Estilos estilos, ReporteDashboardDTO reporte) {
        Sheet hoja = libro.createSheet("Resumen");
        Indicadores ind = reporte.indicadores();

        Row titulo = hoja.createRow(0);
        celda(titulo, 0, "Reporte de ventas - Corvacho's Restaurante", estilos.titulo);
        Row periodo = hoja.createRow(1);
        celda(periodo, 0, "Periodo: " + reporte.periodo().desde().format(FECHA) + " a "
                + reporte.periodo().hasta().format(FECHA) + " (" + reporte.periodo().dias() + " días)", null);

        encabezado(hoja.createRow(3), estilos, "Indicador", "Valor", "Vs. periodo anterior");
        int fila = 4;
        fila = filaIndicador(hoja, fila, estilos, "Ventas totales", ind.ventas(), estilos.moneda, ind.variacionVentas());
        fila = filaIndicador(hoja, fila, estilos, "Pedidos", ind.pedidos(), estilos.entero, ind.variacionPedidos());
        fila = filaIndicador(hoja, fila, estilos, "Ticket promedio", ind.ticketPromedio(), estilos.moneda, ind.variacionTicket());
        fila = filaIndicador(hoja, fila, estilos, "Clientes únicos", ind.clientesUnicos(), estilos.entero, ind.variacionClientes());
        fila = filaIndicador(hoja, fila, estilos, "Platos vendidos", ind.unidadesVendidas(), estilos.entero, null);
        fila = filaIndicador(hoja, fila, estilos, "Ingresos por adicionales", ind.ingresosAdicionales(), estilos.moneda, null);
        fila = filaIndicador(hoja, fila, estilos, "% de ventas por adicionales", ind.porcentajeAdicionales() / 100, estilos.porcentaje, null);
        if (ind.minutosPromedioEntrega() != null) {
            filaIndicador(hoja, fila, estilos, "Tiempo promedio de entrega (min)", ind.minutosPromedioEntrega(), estilos.decimal, null);
        }
        anchos(hoja, 36, 18, 22);
    }

    private int filaIndicador(Sheet hoja, int numero, Estilos estilos, String nombre, double valor, CellStyle estilo,
            Double variacion) {
        Row fila = hoja.createRow(numero);
        celda(fila, 0, nombre, estilos.texto);
        numero(fila, 1, valor, estilo);
        if (variacion != null) {
            numero(fila, 2, variacion / 100, estilos.porcentaje);
        } else {
            celda(fila, 2, "-", estilos.texto);
        }
        return numero + 1;
    }

    private void hojaVentasPorDia(Workbook libro, Estilos estilos, List<VentaDia> ventas) {
        Sheet hoja = libro.createSheet("Ventas por día");
        encabezado(hoja.createRow(0), estilos, "Fecha", "Pedidos", "Ventas");
        int numero = 1;
        for (VentaDia venta : ventas) {
            Row fila = hoja.createRow(numero++);
            celda(fila, 0, venta.fecha().format(FECHA), estilos.texto);
            numero(fila, 1, venta.pedidos(), estilos.entero);
            numero(fila, 2, venta.ventas(), estilos.moneda);
        }
        // Fila de totales con formulas de Excel
        Row total = hoja.createRow(numero);
        celda(total, 0, "Total", estilos.encabezado);
        formula(total, 1, "SUM(B2:B" + numero + ")", estilos.enteroTotal);
        formula(total, 2, "SUM(C2:C" + numero + ")", estilos.monedaTotal);
        hoja.createFreezePane(0, 1);
        anchos(hoja, 14, 12, 18);
    }

    private void hojaRanking(Workbook libro, Estilos estilos, String nombreHoja, String columnaNombre,
            String columnaDetalle, List<Ranking> ranking) {
        Sheet hoja = libro.createSheet(nombreHoja);
        boolean conDetalle = columnaDetalle != null;
        if (conDetalle) {
            encabezado(hoja.createRow(0), estilos, "#", columnaNombre, columnaDetalle, "Cantidad", "Total");
        } else {
            encabezado(hoja.createRow(0), estilos, "#", columnaNombre, "Cantidad", "Total");
        }
        int numero = 1;
        for (Ranking item : ranking) {
            Row fila = hoja.createRow(numero);
            int col = 0;
            numero(fila, col++, numero, estilos.entero);
            celda(fila, col++, item.nombre(), estilos.texto);
            if (conDetalle) {
                celda(fila, col++, item.detalle(), estilos.texto);
            }
            numero(fila, col++, item.cantidad(), estilos.entero);
            numero(fila, col, item.total(), estilos.moneda);
            numero++;
        }
        hoja.createFreezePane(0, 1);
        if (conDetalle) {
            anchos(hoja, 6, 32, 30, 12, 18);
        } else {
            anchos(hoja, 6, 32, 12, 18);
        }
    }

    // ---------------------------------------------------------------- ayudas

    private void encabezado(Row fila, Estilos estilos, String... titulos) {
        for (int i = 0; i < titulos.length; i++) {
            celda(fila, i, titulos[i], estilos.encabezado);
        }
    }

    private void celda(Row fila, int columna, String valor, CellStyle estilo) {
        Cell celda = fila.createCell(columna);
        celda.setCellValue(valor != null ? valor : "");
        if (estilo != null) {
            celda.setCellStyle(estilo);
        }
    }

    private void numero(Row fila, int columna, double valor, CellStyle estilo) {
        Cell celda = fila.createCell(columna);
        celda.setCellValue(valor);
        celda.setCellStyle(estilo);
    }

    private void formula(Row fila, int columna, String formula, CellStyle estilo) {
        Cell celda = fila.createCell(columna);
        celda.setCellFormula(formula);
        celda.setCellStyle(estilo);
    }

    // Anchos fijos en caracteres (autoSizeColumn necesita fuentes del sistema y es lento)
    private void anchos(Sheet hoja, int... caracteres) {
        for (int i = 0; i < caracteres.length; i++) {
            hoja.setColumnWidth(i, caracteres[i] * 256);
        }
    }

    // Estilos del libro con los colores del restaurante (azul oscuro y dorado)
    private static final class Estilos {
        final CellStyle titulo;
        final CellStyle encabezado;
        final CellStyle texto;
        final CellStyle entero;
        final CellStyle decimal;
        final CellStyle moneda;
        final CellStyle porcentaje;
        final CellStyle enteroTotal;
        final CellStyle monedaTotal;

        Estilos(Workbook libro) {
            Font fuenteTitulo = libro.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 16);
            titulo = libro.createCellStyle();
            titulo.setFont(fuenteTitulo);

            Font fuenteEncabezado = libro.createFont();
            fuenteEncabezado.setBold(true);
            fuenteEncabezado.setColor(IndexedColors.WHITE.getIndex());
            encabezado = libro.createCellStyle();
            encabezado.setFont(fuenteEncabezado);
            encabezado.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
            encabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            bordes(encabezado);

            texto = libro.createCellStyle();
            bordes(texto);

            short formatoMoneda = libro.createDataFormat().getFormat("\"$\"#,##0");
            entero = conFormato(libro, libro.createDataFormat().getFormat("#,##0"), false);
            decimal = conFormato(libro, libro.createDataFormat().getFormat("#,##0.0"), false);
            moneda = conFormato(libro, formatoMoneda, false);
            porcentaje = conFormato(libro, libro.createDataFormat().getFormat("0.0%"), false);
            enteroTotal = conFormato(libro, libro.createDataFormat().getFormat("#,##0"), true);
            monedaTotal = conFormato(libro, formatoMoneda, true);
        }

        private static CellStyle conFormato(Workbook libro, short formato, boolean negrita) {
            CellStyle estilo = libro.createCellStyle();
            estilo.setDataFormat(formato);
            bordes(estilo);
            if (negrita) {
                Font fuente = libro.createFont();
                fuente.setBold(true);
                estilo.setFont(fuente);
                estilo.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            return estilo;
        }

        private static void bordes(CellStyle estilo) {
            estilo.setBorderBottom(BorderStyle.THIN);
            estilo.setBorderTop(BorderStyle.THIN);
            estilo.setBorderLeft(BorderStyle.THIN);
            estilo.setBorderRight(BorderStyle.THIN);
        }
    }
}
