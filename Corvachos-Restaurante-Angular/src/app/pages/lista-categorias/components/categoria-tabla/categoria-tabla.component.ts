import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Categoria } from '../../../../models/categoria.model';

@Component({
  selector: 'app-categoria-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './categoria-tabla.component.html',
  styleUrl: './categoria-tabla.component.scss'
})
export class CategoriaTablaComponent {

  @Input() categorias: Categoria[] = [];
  @Output() eliminar = new EventEmitter<number>();

  onEliminar(id: number): void {
    this.eliminar.emit(id);
  }
}