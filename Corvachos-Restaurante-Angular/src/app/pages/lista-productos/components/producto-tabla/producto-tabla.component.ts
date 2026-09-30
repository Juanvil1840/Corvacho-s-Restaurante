import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Producto } from '../../../../models/producto.model';

@Component({
  selector: 'app-producto-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './producto-tabla.component.html',
  styleUrl: './producto-tabla.component.scss'
})
export class ProductoTablaComponent {

  @Input() productos!: Producto[];
  @Output() eliminar = new EventEmitter<number>();

  eliminarProducto(id: number): void {
    this.eliminar.emit(id);
  }

}