import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Adicional } from '../models/adicional.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdicionalService {

  private apiUrl = `${environment.apiUrl}/adicionales`;

  constructor(private http: HttpClient) { }

  getAdicionales(): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  getAdicionalById(id: number): Observable<Adicional> {
    return this.http.get<Adicional>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  getAdicionalesPorCategoria(categoriaId: number): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(`${this.apiUrl}/categoria/${categoriaId}`).pipe(
      catchError(this.manejarError)
    );
  }

  getAdicionalesDisponiblesPorCategoria(categoriaId: number): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(`${this.apiUrl}/categoria/${categoriaId}/disponibles`).pipe(
      catchError(this.manejarError)
    );
  }

  agregarAdicional(adicional: Adicional): Observable<Adicional> {
    return this.http.post<Adicional>(this.apiUrl, adicional).pipe(
      catchError(this.manejarError)
    );
  }

  actualizarAdicional(adicional: Adicional): Observable<Adicional> {
    return this.http.put<Adicional>(
      `${this.apiUrl}/${adicional.id_adicional}`,
      adicional
    ).pipe(
      catchError(this.manejarError)
    );
  }

  eliminarAdicional(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  private manejarError(error: HttpErrorResponse) {
    if (error.status === 0) {
      console.error('Error de conexión con Spring Boot', error);
    } else {
      console.error(`Error HTTP ${error.status}: ${error.message}`);
    }
    return throwError(() => error);
  }
}
