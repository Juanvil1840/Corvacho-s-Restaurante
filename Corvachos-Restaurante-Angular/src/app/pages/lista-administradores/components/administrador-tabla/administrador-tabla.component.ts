import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Administrador } from '../../../../models/administrador.model';

// Componente que renderiza la tabla de administradores
@Component({
  selector: 'app-administrador-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './administrador-tabla.component.html',
  styleUrl: './administrador-tabla.component.scss'
})
export class AdministradorTablaComponent {
  // Lista de administradores a mostrar en la tabla
  @Input() administradores: Administrador[] = [];
  // Evento para notificar al componente padre la eliminación de un administrador
  @Output() eliminar = new EventEmitter<number>();

  // Emite el ID del administrador que se desea eliminar
  onEliminar(id: number): void {
    this.eliminar.emit(id);
  }
}
