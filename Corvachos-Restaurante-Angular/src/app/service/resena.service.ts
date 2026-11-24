import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CrearResena, Resena } from '../models/resena.model';

@Injectable({ providedIn: 'root' })
export class ResenaService {
  private readonly apiUrl = `${environment.apiUrl}/resenas`;

  constructor(private http: HttpClient) {}

  getPorCliente(clienteId: number): Observable<Resena[]> {
    return this.http.get<Resena[]>(`${this.apiUrl}/cliente/${clienteId}`);
  }

  crear(pedidoId: number, resena: CrearResena): Observable<Resena> {
    return this.http.post<Resena>(`${this.apiUrl}/pedido/${pedidoId}`, resena);
  }

  getTodas(): Observable<Resena[]> {
    return this.http.get<Resena[]>(this.apiUrl);
  }
}
