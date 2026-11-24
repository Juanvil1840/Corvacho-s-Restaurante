package com.example.demo.service;

import java.time.LocalDate;

import com.example.demo.dto.reportes.ReporteDashboardDTO;

// Servicio de reportes de ventas para el dashboard del administrador
public interface ReporteService {

    // Calcula los indicadores, graficas y rankings de los pedidos creados entre las dos fechas (incluidas)
    ReporteDashboardDTO generarDashboard(LocalDate desde, LocalDate hasta);

    // Genera el mismo reporte como archivo Excel (.xlsx)
    byte[] exportarExcel(LocalDate desde, LocalDate hasta);
}
