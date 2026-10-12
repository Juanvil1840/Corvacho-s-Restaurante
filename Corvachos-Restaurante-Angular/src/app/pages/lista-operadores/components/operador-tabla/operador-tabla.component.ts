import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Operador } from '../../../../models/operador.model';

@Component({
  selector: 'app-operador-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './operador-tabla.component.html',
  styleUrl: './operador-tabla.component.scss'
})
export class OperadorTablaComponent {
  @Input() operadores: Operador[] = [];
  @Output() eliminar = new EventEmitter<number>();

  onEliminar(id: number): void {
    this.eliminar.emit(id);
  }
}

