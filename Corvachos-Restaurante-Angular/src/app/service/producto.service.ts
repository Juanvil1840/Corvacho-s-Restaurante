
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Producto } from '../models/producto.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {

  private apiUrl = `${environment.apiUrl}/productos`;

  constructor(private http: HttpClient) { }

  // Consultar productos (por defecto todos=true para ver la lista completa en gestión)
  getProductos(todos: boolean = true): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}?todos=${todos}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Consultar solo productos disponibles
  getProductosDisponibles(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}?todos=false`).pipe(
      catchError(this.manejarError)
    );
  }

  // Consultar un producto por ID
  getProductoById(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Crear producto
  agregarProducto(producto: Producto): Observable<Producto> {
    return this.http.post<Producto>(this.apiUrl, producto).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualizar producto
  actualizarProducto(producto: Producto): Observable<Producto> {
    return this.http.put<Producto>(
      `${this.apiUrl}/${producto.id}`,
      producto
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Eliminar producto
  eliminarProducto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Manejo básico de errores HTTP
  private manejarError(error: HttpErrorResponse) {
    if (error.status === 0) {
      console.error('Error de conexión con Spring Boot', error);
    } else {
      console.error(
        `Error HTTP ${error.status}: ${error.message}`
      );
    }

    return throwError(() => error);
  }
}
