import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cupon, CuponValidado } from '../models/cupon.model';
import { environment } from '../../environments/environment';

// Servicio para los cupones de descuento: administración (admin) y validación en el carrito (cliente)
@Injectable({
  providedIn: 'root'
})
export class CuponService {

  private apiUrl = `${environment.apiUrl}/cupones`;

  constructor(private http: HttpClient) { }

  getCupones(): Observable<Cupon[]> {
    return this.http.get<Cupon[]>(this.apiUrl);
  }

  getCuponById(id: number): Observable<Cupon> {
    return this.http.get<Cupon>(`${this.apiUrl}/${id}`);
  }

  agregarCupon(cupon: Cupon): Observable<Cupon> {
    return this.http.post<Cupon>(this.apiUrl, cupon);
  }

  actualizarCupon(cupon: Cupon): Observable<Cupon> {
    return this.http.put<Cupon>(`${this.apiUrl}/${cupon.id_cupon}`, cupon);
  }

  eliminarCupon(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Prueba un código con el valor actual del carrito; si no sirve, el error trae el motivo en error.mensaje
  validarCupon(codigo: string, subtotal: number): Observable<CuponValidado> {
    const params = new HttpParams().set('codigo', codigo).set('subtotal', subtotal);
    return this.http.get<CuponValidado>(`${this.apiUrl}/validar`, { params });
  }
}
