import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Operador } from '../models/operador.model';
import { environment } from '../../environments/environment';

// Servicio para gestionar operadores y su autenticación en el sistema
@Injectable({
  providedIn: 'root'
})
export class OperadorService {

  // URL base del endpoint de operadores
  private apiUrl = `${environment.apiUrl}/operadores`;

  constructor(private http: HttpClient) { }

  // Consultar la lista de todos los operadores
  getOperadores(): Observable<Operador[]> {
    return this.http.get<Operador[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  // Iniciar sesión de operador con usuario y contraseña
  login(usuario: string, contrasena: string): Observable<Operador> {
  return this.http.post<Operador>(`${this.apiUrl}/login`, { usuario, contrasena }).pipe(
    catchError(this.manejarError)
  );
}

  // Consultar un operador por su ID
  getOperadorById(id: number): Observable<Operador> {
    return this.http.get<Operador>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Registrar un nuevo operador
  agregarOperador(operador: Operador): Observable<Operador> {
    return this.http.post<Operador>(this.apiUrl, operador).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualizar los datos de un operador existente
  actualizarOperador(operador: Operador): Observable<Operador> {
    return this.http.put<Operador>(
      `${this.apiUrl}/${operador.id_operador}`,
      operador
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Eliminar un operador por su ID
  eliminarOperador(id: number): Observable<void> {
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
