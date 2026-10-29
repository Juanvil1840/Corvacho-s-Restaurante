import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Pedido, PedidoGuardar, PedidoLista } from '../models/pedido.model';
import { PedidoHistorial } from '../models/pedidoHistorial.model';
import { environment } from '../../environments/environment';

// Servicio para la gestion de Pedidos
@Injectable({
  providedIn: 'root'
})
export class PedidoService {

  private apiUrl = `${environment.apiUrl}/pedidos`;

  constructor(private http: HttpClient) { }

  // Obtiene la lista de pedidos, opcionalmente solo los activos
  getPedidos(activos: boolean = false): Observable<PedidoLista[]> {
    return this.http.get<PedidoLista[]>(`${this.apiUrl}?activos=${activos}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene el historial de pedidos de un cliente por su id
  getPedidosPorCliente(clienteId: number): Observable<PedidoHistorial[]> {
    return this.http.get<PedidoHistorial[]>(`${this.apiUrl}/cliente/${clienteId}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene el detalle de un pedido por su id
  getPedidoById(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Crea un nuevo pedido
  agregarPedido(pedido: PedidoGuardar): Observable<Pedido> {
    return this.http.post<Pedido>(this.apiUrl, pedido).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualiza un pedido existente por su id
  actualizarPedido(id: number, pedido: PedidoGuardar): Observable<Pedido> {
    return this.http.put<Pedido>(`${this.apiUrl}/${id}`, pedido).pipe(
      catchError(this.manejarError)
    );
  }

  // Elimina un pedido por su id
  eliminarPedido(id: number): Observable<void> {
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