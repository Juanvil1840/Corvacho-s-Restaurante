import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Domiciliario } from '../models/domiciliario.model';
import { environment } from '../../environments/environment';

// Servicio para gestionar las operaciones CRUD de domiciliarios
@Injectable({
  providedIn: 'root'
})
export class DomiciliarioService {

  // URL base del endpoint de domiciliarios
  private apiUrl = `${environment.apiUrl}/domiciliarios`;

  constructor(private http: HttpClient) { }

  // Consultar todos los domiciliarios registrados
  getDomiciliarios(): Observable<Domiciliario[]> {
    return this.http.get<Domiciliario[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  // Consultar un domiciliario específico por su ID
  getDomiciliarioById(id: number): Observable<Domiciliario> {
    return this.http.get<Domiciliario>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Registrar un nuevo domiciliario
  agregarDomiciliario(domiciliario: Domiciliario): Observable<Domiciliario> {
    return this.http.post<Domiciliario>(this.apiUrl, domiciliario).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualizar la información de un domiciliario existente
  actualizarDomiciliario(domiciliario: Domiciliario): Observable<Domiciliario> {
    return this.http.put<Domiciliario>(
      `${this.apiUrl}/${domiciliario.id_domiciliario}`,
      domiciliario
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Eliminar un domiciliario por su ID
  eliminarDomiciliario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
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
