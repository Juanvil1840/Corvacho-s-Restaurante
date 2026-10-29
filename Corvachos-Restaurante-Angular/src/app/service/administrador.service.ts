import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Administrador } from '../models/administrador.model';
import { environment } from '../../environments/environment';

// Servicio para gestionar administradores y su inicio de sesión
@Injectable({
  providedIn: 'root'
})
export class AdministradorService {

  // URL base del endpoint de administradores
  private apiUrl = `${environment.apiUrl}/administradores`;

  constructor(private http: HttpClient) { }

  // Consultar todos los administradores registrados
  getAdministradores(): Observable<Administrador[]> {
    return this.http.get<Administrador[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  // Consultar un administrador por su ID
  getAdministradorById(id: number): Observable<Administrador> {
    return this.http.get<Administrador>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Registrar un nuevo administrador
  agregarAdministrador(administrador: Administrador): Observable<Administrador> {
    return this.http.post<Administrador>(this.apiUrl, administrador).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualizar los datos de un administrador existente
  actualizarAdministrador(administrador: Administrador): Observable<Administrador> {
    return this.http.put<Administrador>(
      `${this.apiUrl}/${administrador.id_administrador}`,
      administrador
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Eliminar un administrador por su ID
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
