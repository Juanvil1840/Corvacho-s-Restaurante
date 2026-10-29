import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Categoria } from '../../../../models/categoria.model';

// Componente que muestra la tabla con el listado de categorías
@Component({
  selector: 'app-categoria-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './categoria-tabla.component.html',
  styleUrl: './categoria-tabla.component.scss'
})
export class CategoriaTablaComponent {

  // Categorías recibidas del componente padre
  @Input() categorias: Categoria[] = [];
  // Evento emitido al solicitar la eliminación de una categoría
  @Output() eliminar = new EventEmitter<number>();

  // Emite el ID de la categoría seleccionada para eliminar
  onEliminar(id: number): void {
    this.eliminar.emit(id);
  }
}
