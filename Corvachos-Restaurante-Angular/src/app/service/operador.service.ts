import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Operador } from '../models/operador.model';
import { environment } from '../../environments/environment';

// Servicio para la gestion de Operadores 
@Injectable({
  providedIn: 'root'
})
export class OperadorService {

  private apiUrl = `${environment.apiUrl}/operadores`;

  constructor(private http: HttpClient) { }

  // Obtiene todos los operadores
  getOperadores(): Observable<Operador[]> {
    return this.http.get<Operador[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  // Login del operador
  login(usuario: string, contrasena: string): Observable<Operador> {
    return this.http.post<Operador>(`${this.apiUrl}/login`, { usuario, contrasena }).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene un operador por su id
  getOperadorById(id: number): Observable<Operador> {
    return this.http.get<Operador>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Crea un nuevo operador
  agregarOperador(operador: Operador): Observable<Operador> {
    return this.http.post<Operador>(this.apiUrl, operador).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualiza un operador existente
  actualizarOperador(operador: Operador): Observable<Operador> {
    return this.http.put<Operador>(
      `${this.apiUrl}/${operador.id_operador}`,
      operador
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Elimina un operador por su id
  eliminarOperador(id: number): Observable<void> {
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