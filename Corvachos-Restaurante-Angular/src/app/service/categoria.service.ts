import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Categoria } from '../models/categoria.model';
import { environment } from '../../environments/environment';

// Servicio para la gestion de Categorias 
@Injectable({
  providedIn: 'root'
})
export class CategoriaService {

  private apiUrl = `${environment.apiUrl}/categorias`;

  constructor(private http: HttpClient) { }

  // Obtiene todas las categorias
  getCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  // Obtiene una categoria por su id
  getCategoriaById(id: number): Observable<Categoria> {
    return this.http.get<Categoria>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  // Crea una nueva categoria
  agregarCategoria(categoria: Categoria): Observable<Categoria> {
    return this.http.post<Categoria>(this.apiUrl, categoria).pipe(
      catchError(this.manejarError)
    );
  }

  // Actualiza una categoria existente
  actualizarCategoria(categoria: Categoria): Observable<Categoria> {
    return this.http.put<Categoria>(
      `${this.apiUrl}/${categoria.id}`,
      categoria
    ).pipe(
      catchError(this.manejarError)
    );
  }

  // Elimina una categoria por su id
  eliminarCategoria(id: number): Observable<void> {
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