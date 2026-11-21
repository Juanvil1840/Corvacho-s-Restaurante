package com.example.demo.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.reportes.ReporteDashboardDTO;
import com.example.demo.service.ReporteService;

// Reportes de ventas para el dashboard del administrador (solo rol ADMIN)
@RestController
@RequestMapping("/api/reportes")
@PreAuthorize("hasAuthority('ADMIN')")
public class ReporteRestController {

    // Si no se envian fechas se muestran los ultimos 30 dias
    private static final int DIAS_POR_DEFECTO = 30;

    private static final MediaType EXCEL =
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ReporteService reporteService;

    public ReporteRestController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    // GET /api/reportes/dashboard?desde=2026-10-01&hasta=2026-10-31
    @GetMapping("/dashboard")
    public ReporteDashboardDTO dashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        LocalDate fin = hasta != null ? hasta : LocalDate.now();
        LocalDate inicio = desde != null ? desde : fin.minusDays(DIAS_POR_DEFECTO - 1);
        return reporteService.generarDashboard(inicio, fin);
    }

    // GET /api/reportes/excel?desde=...&hasta=...  -> descarga un .xlsx
    @GetMapping("/excel")
    public ResponseEntity<byte[]> excel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        LocalDate fin = hasta != null ? hasta : LocalDate.now();
        LocalDate inicio = desde != null ? desde : fin.minusDays(DIAS_POR_DEFECTO - 1);
        byte[] archivo = reporteService.exportarExcel(inicio, fin);
        String nombre = "reporte-corvachos_" + inicio + "_a_" + fin + ".xlsx";
        return ResponseEntity.ok()
                .contentType(EXCEL)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(nombre).build().toString())
                .body(archivo);
    }
}
