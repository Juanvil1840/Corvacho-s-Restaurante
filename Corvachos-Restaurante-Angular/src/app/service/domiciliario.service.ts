import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Domiciliario } from '../models/domiciliario.model';
import { environment } from '../../environments/environment';

// Servicio para la gestion de Domiciliarios 
@Injectable({
  providedIn: 'root'
})
export class DomiciliarioService {

  private apiUrl = `${environment.apiUrl}/domiciliarios`;

  constructor(private http: HttpClient) { }

  // Obtiene todos los domiciliarios
  getDomiciliarios(): Observable<Domiciliario[]> {
    return this.http.get<Domiciliario[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene solo los domiciliarios disponibles
  getDomiciliariosDisponibles(): Observable<Domiciliario[]> {
    return this.http.get<Domiciliario[]>(`${this.apiUrl}/disponibles`).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene un domiciliario por su id
  getDomiciliarioById(id: number): Observable<Domiciliario> {
    return this.http.get<Domiciliario>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Crea un nuevo domiciliario
  agregarDomiciliario(domiciliario: Domiciliario): Observable<Domiciliario> {
    return this.http.post<Domiciliario>(this.apiUrl, domiciliario).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualiza un domiciliario existente
  actualizarDomiciliario(domiciliario: Domiciliario): Observable<Domiciliario> {
    return this.http.put<Domiciliario>(
      `${this.apiUrl}/${domiciliario.id_domiciliario}`,
      domiciliario
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Elimina un domiciliario por su id
  eliminarDomiciliario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Manejo de errores 
  private manejarError(error: HttpErrorResponse) {
    if (error.status === 0) {
      console.error('Error de conexion con Spring Boot', error);
    } else {
      console.error(`Error HTTP ${error.status}: ${error.message}`);
    }
    return throwError(() => error);
  }
}