import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Operador } from '../models/operador.model';

@Injectable({
  providedIn: 'root'
})
export class OperadorService {

  private apiUrl = 'http://localhost:8090/api/operadores';

  constructor(private http: HttpClient) { }

  getOperadores(): Observable<Operador[]> {
    return this.http.get<Operador[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  getOperadorById(id: number): Observable<Operador> {
    return this.http.get<Operador>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  agregarOperador(operador: Operador): Observable<Operador> {
    return this.http.post<Operador>(this.apiUrl, operador).pipe(
      catchError(this.manejarError)
    );
  }

  actualizarOperador(operador: Operador): Observable<Operador> {
    return this.http.put<Operador>(
      `${this.apiUrl}/${operador.id_operador}`,
      operador
    ).pipe(
      catchError(this.manejarError)
    );
  }

  eliminarOperador(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
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

