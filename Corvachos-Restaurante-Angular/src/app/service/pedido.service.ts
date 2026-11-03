import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Pedido, PedidoGuardar, PedidoLista } from '../models/pedido.model';
import { PedidoHistorial } from '../models/pedidoHistorial.model';
import { environment } from '../../environments/environment';

// Servicio para gestionar la creación, consulta, actualización y eliminación de pedidos
@Injectable({
  providedIn: 'root'
})
export class PedidoService {

  // URL base del endpoint de pedidos
  private apiUrl = `${environment.apiUrl}/pedidos`;

  constructor(private http: HttpClient) { }

  // Consultar la lista de pedidos (permite filtrar solo los pedidos activos)
  getPedidos(activos: boolean = false): Observable<PedidoLista[]> {
    return this.http.get<PedidoLista[]>(`${this.apiUrl}?activos=${activos}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Consultar el historial de pedidos realizados por un cliente específico
  getPedidosPorCliente(clienteId: number): Observable<PedidoHistorial[]> {
    return this.http.get<PedidoHistorial[]>(`${this.apiUrl}/cliente/${clienteId}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Consultar el detalle completo de un pedido por su ID
  getPedidoById(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Crear un nuevo pedido en el sistema
  agregarPedido(pedido: PedidoGuardar): Observable<Pedido> {
    return this.http.post<Pedido>(this.apiUrl, pedido).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualizar el estado o datos de un pedido existente
  actualizarPedido(id: number, pedido: PedidoGuardar): Observable<Pedido> {
    return this.http.put<Pedido>(`${this.apiUrl}/${id}`, pedido).pipe(
      catchError(this.manejarError)
    );
  }

  // Eliminar un pedido por su ID
  eliminarPedido(id: number): Observable<void> {
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
