import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Adicional } from '../../../../models/adicional.model';

// Componente presentacional que muestra la tabla de adicionales
@Component({
  selector: 'app-adicional-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './adicional-tabla.component.html',
  styleUrl: './adicional-tabla.component.scss'
})
export class AdicionalTablaComponent {

  // Lista de adicionales recibida desde el componente padre
  @Input() adicionales: Adicional[] = [];
  // Evento emitido hacia el padre cuando se solicita eliminar un adicional
  @Output() eliminar = new EventEmitter<number>();

  // Emite el ID del adicional a eliminar
  onEliminar(id: number): void {
    this.eliminar.emit(id);
  }
}
