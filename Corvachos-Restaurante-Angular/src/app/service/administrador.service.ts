import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Administrador } from '../models/administrador.model';

@Injectable({
  providedIn: 'root'
})
export class AdministradorService {

  private apiUrl = 'http://localhost:8090/api/administradores';

  constructor(private http: HttpClient) { }

  getAdministradores(): Observable<Administrador[]> {
    return this.http.get<Administrador[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  getAdministradorById(id: number): Observable<Administrador> {
    return this.http.get<Administrador>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  agregarAdministrador(administrador: Administrador): Observable<Administrador> {
    return this.http.post<Administrador>(this.apiUrl, administrador).pipe(
      catchError(this.manejarError)
    );
  }

  actualizarAdministrador(administrador: Administrador): Observable<Administrador> {
    return this.http.put<Administrador>(
      `${this.apiUrl}/${administrador.id_administrador}`,
      administrador
    ).pipe(
      catchError(this.manejarError)
    );
  }

  eliminarAdministrador(id: number): Observable<void> {
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

