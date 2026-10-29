import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Operador } from '../../../../models/operador.model';

// Componente que renderiza la tabla de operadores
@Component({
  selector: 'app-operador-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './operador-tabla.component.html',
  styleUrl: './operador-tabla.component.scss'
})
export class OperadorTablaComponent {
  // Lista de operadores recibida desde el componente padre
  @Input() operadores: Operador[] = [];
  // Evento que notifica al padre cuando se desea eliminar un operador
  @Output() eliminar = new EventEmitter<number>();

  // Emite el ID del operador seleccionado para eliminar
  onEliminar(id: number): void {
    this.eliminar.emit(id);
  }
}
