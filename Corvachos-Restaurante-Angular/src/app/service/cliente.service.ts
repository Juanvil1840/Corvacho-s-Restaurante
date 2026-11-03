import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Cliente } from '../models/cliente.model';
import { environment } from '../../environments/environment';

// Servicio para gestionar clientes, estado de su cuenta e inicio de sesión
@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  // URL base del endpoint de clientes
  private apiUrl = `${environment.apiUrl}/clientes`;

  constructor(private http: HttpClient) { }

  // Consultar la lista de todos los clientes
  getClientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  // Consultar la información de un cliente por su ID
  getClienteById(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Registrar un nuevo cliente
  agregarCliente(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, cliente).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualizar los datos de un cliente existente
  actualizarCliente(cliente: Cliente): Observable<Cliente> {
    return this.http.put<Cliente>(
      `${this.apiUrl}/${cliente.clienteId}`,
      cliente
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Desactivar la cuenta de un cliente sin eliminar su registro
  desactivarCliente(id: number): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${id}/desactivar`, {}).pipe(
      catchError(this.manejarError)
    );
  }

  // Reactivar la cuenta de un cliente previamente desactivado
  reactivarCliente(id: number): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${id}/reactivar`, {}).pipe(
      catchError(this.manejarError)
    );
  }

  // Eliminar un cliente por su ID
  eliminarCliente(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Iniciar sesión de cliente con correo y contraseña
  login(correo: string, contrasena: string): Observable<Cliente> {
    return this.http.post<Cliente>(`${this.apiUrl}/login`, { correo, contrasena }).pipe(
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
