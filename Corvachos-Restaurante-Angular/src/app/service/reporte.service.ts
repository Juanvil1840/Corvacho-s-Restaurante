import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable, catchError, map, throwError } from 'rxjs';
import { RangoFechas, ReporteDashboard } from '../models/reporte.model';
import { environment } from '../../environments/environment';

// Archivo descargado desde el backend junto con el nombre que sugiere el servidor
export interface ArchivoDescargado {
  nombre: string;
  contenido: Blob;
}

// Servicio para consultar los reportes del negocio (solo administradores)
@Injectable({
  providedIn: 'root'
})
export class ReporteService {

  // URL base del endpoint de reportes
  private apiUrl = `${environment.apiUrl}/reportes`;

  constructor(private http: HttpClient) { }

  // Consultar todos los indicadores y graficas del dashboard para un rango de fechas
  getDashboard(rango: RangoFechas): Observable<ReporteDashboard> {
    return this.http.get<ReporteDashboard>(`${this.apiUrl}/dashboard`, { params: this.parametros(rango) }).pipe(
      catchError(this.manejarError)
    );
  }

  // Descargar el reporte en Excel; el nombre del archivo viene en la cabecera Content-Disposition
  descargarExcel(rango: RangoFechas): Observable<ArchivoDescargado> {
    return this.http.get(`${this.apiUrl}/excel`, {
      params: this.parametros(rango),
      responseType: 'blob',
      observe: 'response'
    }).pipe(
      map((respuesta: HttpResponse<Blob>) => ({
        nombre: this.nombreArchivo(respuesta, rango),
        contenido: respuesta.body ?? new Blob()
      })),
      catchError(this.manejarError)
    );
  }

  private parametros(rango: RangoFechas): HttpParams {
    return new HttpParams().set('desde', rango.desde).set('hasta', rango.hasta);
  }

  // Lee filename="..." de la cabecera; si no viene, arma un nombre con las fechas
  private nombreArchivo(respuesta: HttpResponse<Blob>, rango: RangoFechas): string {
    const cabecera = respuesta.headers.get('Content-Disposition') ?? '';
    const coincidencia = /filename="?([^";]+)"?/i.exec(cabecera);
    return coincidencia ? coincidencia[1] : `reporte-corvachos_${rango.desde}_a_${rango.hasta}.xlsx`;
  }

  // Manejo básico de errores HTTP
  private manejarError(error: HttpErrorResponse) {
    if (error.status === 0) {
      console.error('Error de conexión con Spring Boot', error);
    } else {
      console.error(`Error HTTP ${error.status}: ${error.message}`);
    }
    return throwError(() => error);
  }
}
