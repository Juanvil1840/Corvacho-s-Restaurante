import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Administrador } from '../models/administrador.model';
import { environment } from '../../environments/environment';

// Servicio para la gestion de Administradores
@Injectable({
  providedIn: 'root'
})
export class AdministradorService {

  private apiUrl = `${environment.apiUrl}/administradores`;

  constructor(private http: HttpClient) { }

  // Obtiene todos los administradores
  getAdministradores(): Observable<Administrador[]> {
    return this.http.get<Administrador[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene un administrador por su id
  getAdministradorById(id: number): Observable<Administrador> {
    return this.http.get<Administrador>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Crea un nuevo administrador
  agregarAdministrador(administrador: Administrador): Observable<Administrador> {
    return this.http.post<Administrador>(this.apiUrl, administrador).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualiza un administrador existente
  actualizarAdministrador(administrador: Administrador): Observable<Administrador> {
    return this.http.put<Administrador>(
      `${this.apiUrl}/${administrador.id_administrador}`,
      administrador
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Elimina un administrador por su id
  eliminarAdministrador(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Login del administrador
  login(usuario: string, contrasena: string): Observable<Administrador> {
    return this.http.post<Administrador>(`${this.apiUrl}/login`, { usuario, contrasena }).pipe(
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