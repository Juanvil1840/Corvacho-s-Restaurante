import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Cliente } from '../../../../models/cliente.model';

// Componente que muestra los datos personales del cliente y acciones de cuenta
@Component({
  selector: 'app-perfil-info',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './perfil-info.component.html',
  styleUrl: './perfil-info.component.scss'
})
export class PerfilInfoComponent {

  // Información del cliente recibida desde el componente padre
  @Input() cliente!: Cliente;

  // Eventos para notificar la eliminación de la cuenta o el cierre de sesión
  @Output() eliminar = new EventEmitter<void>();
  @Output() cerrar = new EventEmitter<void>();

  // Emite el evento para eliminar la cuenta del cliente
  eliminarCuenta(): void {
    this.eliminar.emit();
  }

  // Emite el evento para cerrar la sesión actual
  cerrarSesion(): void {
    this.cerrar.emit();
  }

}
