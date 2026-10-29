import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Domiciliario } from '../models/domiciliario.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DomiciliarioService {

  private apiUrl = `${environment.apiUrl}/domiciliarios`;

  constructor(private http: HttpClient) { }

  getDomiciliarios(): Observable<Domiciliario[]> {
    return this.http.get<Domiciliario[]>(this.apiUrl).pipe(
      catchError(this.manejarError)
    );
  }

  getDomiciliarioById(id: number): Observable<Domiciliario> {
    return this.http.get<Domiciliario>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.manejarError)
    );
  }

  agregarDomiciliario(domiciliario: Domiciliario): Observable<Domiciliario> {
    return this.http.post<Domiciliario>(this.apiUrl, domiciliario).pipe(
      catchError(this.manejarError)
    );
  }

  actualizarDomiciliario(domiciliario: Domiciliario): Observable<Domiciliario> {
    return this.http.put<Domiciliario>(
      `${this.apiUrl}/${domiciliario.id_domiciliario}`,
      domiciliario
    ).pipe(
      catchError(this.manejarError)
    );
  }

  eliminarDomiciliario(id: number): Observable<void> {
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
