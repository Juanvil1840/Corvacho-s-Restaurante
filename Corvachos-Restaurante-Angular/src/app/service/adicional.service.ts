import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Adicional } from '../models/adicional.model';
import { environment } from '../../environments/environment';

// Servicio para gestionar las operaciones CRUD y consultas de adicionales en el backend
@Injectable({
  providedIn: 'root'
})
export class AdicionalService {

  // URL base del endpoint de adicionales
  private apiUrl = `${environment.apiUrl}/adicionales`;

  constructor(private http: HttpClient) { }

  // Consultar la lista completa de adicionales
  getAdicionales(): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  // Consultar un adicional específico por su ID
  getAdicionalById(id: number): Observable<Adicional> {
    return this.http.get<Adicional>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Consultar todos los adicionales asociados a una categoría
  getAdicionalesPorCategoria(categoriaId: number): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(`${this.apiUrl}/categoria/${categoriaId}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Consultar únicamente los adicionales disponibles de una categoría
  getAdicionalesDisponiblesPorCategoria(categoriaId: number): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(`${this.apiUrl}/categoria/${categoriaId}/disponibles`).pipe(
      catchError(this.manejarError)
    );
  }

  // Crear un nuevo adicional
  agregarAdicional(adicional: Adicional): Observable<Adicional> {
    return this.http.post<Adicional>(this.apiUrl, adicional).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualizar los datos de un adicional existente
  actualizarAdicional(adicional: Adicional): Observable<Adicional> {
    return this.http.put<Adicional>(
      `${this.apiUrl}/${adicional.id_adicional}`,
      adicional
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Eliminar un adicional por su ID
  eliminarAdicional(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Manejo básico de errores HTTP en las peticiones al backend
  private manejarError(error: HttpErrorResponse) {
    if (error.status === 0) {
      console.error('Error de conexión con Spring Boot', error);
    } else {
      console.error(`Error HTTP ${error.status}: ${error.message}`);
    }
    return throwError(() => error);
  }
}
