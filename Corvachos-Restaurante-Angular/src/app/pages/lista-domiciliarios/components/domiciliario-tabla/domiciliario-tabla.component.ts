import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Domiciliario } from '../../../../models/domiciliario.model';

@Component({
  selector: 'app-domiciliario-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './domiciliario-tabla.component.html',
  styleUrl: './domiciliario-tabla.component.scss'
})
export class DomiciliarioTablaComponent {

  @Input() domiciliarios: Domiciliario[] = [];
  @Output() eliminar = new EventEmitter<number>();

  onEliminar(id: number): void {
    this.eliminar.emit(id);
  }
}
