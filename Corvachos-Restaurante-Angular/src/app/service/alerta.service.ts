import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class AlertaService {

  // Alerta de exito (Dorado y azul Corvacho's)
  exito(mensaje: string, titulo: string = '¡Excelente!'): void {
    Swal.fire({
      title: titulo,
      text: mensaje,
      icon: 'success',
      confirmButtonColor: '#cd9e46', // Dorado Corvacho's
      background: '#001c2a',         // Fondo oscuro
      color: '#fcfafa',              // Texto claro
      confirmButtonText: 'Aceptar'
    });
  }

  // Alerta de error
  error(mensaje: string, titulo: string = '¡Ups!'): void {
    Swal.fire({
      title: titulo,
      text: mensaje,
      icon: 'error',
      confirmButtonColor: '#9e2a2b', // Rojo
      background: '#001c2a',
      color: '#fcfafa',
      confirmButtonText: 'Entendido'
    });
  }

  // Alerta de confirmacion (Para cuando van a eliminar algo)
  confirmar(mensaje: string, titulo: string = '¿Estás seguro?'): Promise<boolean> {
    return Swal.fire({
      title: titulo,
      text: mensaje,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#9e2a2b',
      cancelButtonColor: '#6f8896',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      background: '#001c2a',
      color: '#fcfafa'
    }).then((result) => {
      return result.isConfirmed;
    });
  }
}