import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Adicional } from '../models/adicional.model';
import { environment } from '../../environments/environment';

// Servicio para la gestion de Adicionales 
@Injectable({
  providedIn: 'root'
})
export class AdicionalService {

  private apiUrl = `${environment.apiUrl}/adicionales`;

  constructor(private http: HttpClient) { }

  // Obtiene todos los adicionales
  getAdicionales(): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene un adicional por su id
  getAdicionalById(id: number): Observable<Adicional> {
    return this.http.get<Adicional>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene los adicionales asociados a una categoria
  getAdicionalesPorCategoria(categoriaId: number): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(`${this.apiUrl}/categoria/${categoriaId}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene los adicionales disponibles asociados a una categoria
  getAdicionalesDisponiblesPorCategoria(categoriaId: number): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(`${this.apiUrl}/categoria/${categoriaId}/disponibles`).pipe(
      catchError(this.manejarError)
    );
  }

  // Crea un nuevo adicional
  agregarAdicional(adicional: Adicional): Observable<Adicional> {
    return this.http.post<Adicional>(this.apiUrl, adicional).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualiza un adicional existente
  actualizarAdicional(adicional: Adicional): Observable<Adicional> {
    return this.http.put<Adicional>(
      `${this.apiUrl}/${adicional.id_adicional}`,
      adicional
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Elimina un adicional por su id
  eliminarAdicional(id: number): Observable<void> {
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