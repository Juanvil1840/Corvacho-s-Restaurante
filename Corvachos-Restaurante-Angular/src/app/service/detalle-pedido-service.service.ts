import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { DetallePedido } from '../models/detallePedido.model';

@Injectable({
  providedIn: 'root'
})
export class DetallePedidoServiceService {

  private apiUrl = 'http://localhost:8090/api/detalles-pedido';
  private detallesPedidos: DetallePedido[] = [];

  constructor(private http: HttpClient) {
    this.cargarDetalles();
  }

  cargarDetalles(): void {
    this.http.get<DetallePedido[]>(this.apiUrl).subscribe({
      next: (detalles) => {
        this.detallesPedidos = detalles;
      },
      error: (error) => {
        console.error('Error al cargar detalles del pedido:', error);
        this.detallesPedidos = [];
      }
    });
  }

  getDetallesPedidos(): DetallePedido[] {
    return this.detallesPedidos;
  }

  obtenerTodos(): Observable<DetallePedido[]> {
    return this.http.get<DetallePedido[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  obtenerPorPedido(pedidoId: number): Observable<DetallePedido[]> {
    return this.http.get<DetallePedido[]>(`${this.apiUrl}/pedido/${pedidoId}`).pipe(
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