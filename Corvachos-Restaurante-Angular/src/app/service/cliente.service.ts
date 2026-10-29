import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Cliente } from '../models/cliente.model';
import { environment } from '../../environments/environment';

// Servicio para la gestion de Clientes 
@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private apiUrl = `${environment.apiUrl}/clientes`;

  constructor(private http: HttpClient) { }

  // Obtiene todos los clientes
  getClientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene un cliente por su id
  getClienteById(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Crea un nuevo cliente
  agregarCliente(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, cliente).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualiza un cliente existente
  actualizarCliente(cliente: Cliente): Observable<Cliente> {
    return this.http.put<Cliente>(
      `${this.apiUrl}/${cliente.clienteId}`,
      cliente
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Desactiva la cuenta de un cliente
  desactivarCliente(id: number): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${id}/desactivar`, {}).pipe(
      catchError(this.manejarError)
    );
  }

  // Reactiva la cuenta de un cliente
  reactivarCliente(id: number): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${id}/reactivar`, {}).pipe(
      catchError(this.manejarError)
    );
  }

  // Elimina un cliente por su id
  eliminarCliente(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Login del cliente
  login(correo: string, contrasena: string): Observable<Cliente> {
    return this.http.post<Cliente>(`${this.apiUrl}/login`, { correo, contrasena }).pipe(
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