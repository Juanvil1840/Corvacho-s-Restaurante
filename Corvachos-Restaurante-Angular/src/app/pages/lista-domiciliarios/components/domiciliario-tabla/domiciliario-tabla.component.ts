import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Domiciliario } from '../../../../models/domiciliario.model';

// Componente que muestra la tabla de domiciliarios
@Component({
  selector: 'app-domiciliario-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './domiciliario-tabla.component.html',
  styleUrl: './domiciliario-tabla.component.scss'
})
export class DomiciliarioTablaComponent {

  // Lista de domiciliarios recibida del componente padre
  @Input() domiciliarios: Domiciliario[] = [];
  // Evento emitido al solicitar la eliminación de un domiciliario
  @Output() eliminar = new EventEmitter<number>();

  // Emite el ID del domiciliario que se desea eliminar
  onEliminar(id: number): void {
    this.eliminar.emit(id);
  }
}
