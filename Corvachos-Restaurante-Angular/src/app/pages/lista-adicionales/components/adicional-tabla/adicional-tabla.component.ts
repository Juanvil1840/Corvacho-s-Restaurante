import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Adicional } from '../../../../models/adicional.model';

@Component({
  selector: 'app-adicional-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './adicional-tabla.component.html',
  styleUrl: './adicional-tabla.component.scss'
})
export class AdicionalTablaComponent {

  @Input() adicionales: Adicional[] = [];
  @Output() eliminar = new EventEmitter<number>();

  onEliminar(id: number): void {
    this.eliminar.emit(id);
  }
}