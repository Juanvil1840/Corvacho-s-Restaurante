import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Categoria } from '../models/categoria.model';
import { environment } from '../../environments/environment';

// Servicio para gestionar las operaciones CRUD de categorías de productos
@Injectable({
  providedIn: 'root'
})
export class CategoriaService {

  // URL base del endpoint de categorías
  private apiUrl = `${environment.apiUrl}/categorias`;

  constructor(private http: HttpClient) { }

  // Consultar todas las categorías disponibles
  getCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  // Consultar una categoría por su ID
  getCategoriaById(id: number): Observable<Categoria> {
    return this.http.get<Categoria>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Crear una nueva categoría
  agregarCategoria(categoria: Categoria): Observable<Categoria> {
    return this.http.post<Categoria>(this.apiUrl, categoria).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualizar una categoría existente
  actualizarCategoria(categoria: Categoria): Observable<Categoria> {
    return this.http.put<Categoria>(
      `${this.apiUrl}/${categoria.id}`,
      categoria
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Eliminar una categoría por su ID
  eliminarCategoria(id: number): Observable<void> {
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
