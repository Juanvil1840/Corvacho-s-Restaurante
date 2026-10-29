import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Producto } from '../models/producto.model';
import { environment } from '../../environments/environment';

// Servicio para la gestion de Productos 
@Injectable({
  providedIn: 'root'
})
export class ProductoService {

  private apiUrl = `${environment.apiUrl}/productos`;

  constructor(private http: HttpClient) { }

  // Obtiene todos los productos
  getProductos(todos: boolean = true): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}?todos=${todos}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene solo los productos disponibles
  getProductosDisponibles(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}?todos=false`).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene un producto por su id
  getProductoById(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Crea un nuevo producto
  agregarProducto(producto: Producto): Observable<Producto> {
    return this.http.post<Producto>(this.apiUrl, producto).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualiza un producto existente
  actualizarProducto(producto: Producto): Observable<Producto> {
    return this.http.put<Producto>(
      `${this.apiUrl}/${producto.id}`,
      producto
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Elimina un producto por su id
  eliminarProducto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Manejo de errores
  private manejarError(error: HttpErrorResponse) {
    if (error.status === 0) {
      console.error('Error de conexion con Spring Boot', error);
    } else {
      console.error(
        `Error HTTP ${error.status}: ${error.message}`
      );
    }

    return throwError(() => error);
  }
}