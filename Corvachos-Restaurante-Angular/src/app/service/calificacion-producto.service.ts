import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CalificacionProducto } from '../models/calificacionProducto.model';

@Injectable({ providedIn: 'root' })
export class CalificacionProductoService {
  private readonly apiUrl = `${environment.apiUrl}/calificaciones-productos`;

  constructor(private http: HttpClient) {}

  getPorCliente(clienteId: number): Observable<CalificacionProducto[]> {
    return this.http.get<CalificacionProducto[]>(`${this.apiUrl}/cliente/${clienteId}`);
  }

  calificar(pedidoId: number, productoId: number, puntuacion: number): Observable<CalificacionProducto> {
    return this.http.post<CalificacionProducto>(
      `${this.apiUrl}/pedido/${pedidoId}/producto/${productoId}`,
      { puntuacion }
    );
  }
}
