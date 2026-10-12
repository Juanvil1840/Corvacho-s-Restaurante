import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Administrador } from '../../../../models/administrador.model';

@Component({
  selector: 'app-administrador-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './administrador-tabla.component.html',
  styleUrl: './administrador-tabla.component.scss'
})
export class AdministradorTablaComponent {
  @Input() administradores: Administrador[] = [];
  @Output() eliminar = new EventEmitter<number>();

  onEliminar(id: number): void {
    this.eliminar.emit(id);
  }
}

