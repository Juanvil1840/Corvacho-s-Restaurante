import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Producto } from '../../../../models/producto.model';

// Componente que muestra la tabla de productos con acciones de edición, disponibilidad y eliminación
@Component({
  selector: 'app-producto-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './producto-tabla.component.html',
  styleUrl: './producto-tabla.component.scss'
})
export class ProductoTablaComponent {

  // Lista de productos recibida desde el componente padre
  @Input() productos!: Producto[];
  // Eventos para eliminar un producto o cambiar su disponibilidad
  @Output() eliminar = new EventEmitter<number>();
  @Output() toggleDisponible = new EventEmitter<Producto>();

  // Emite el ID del producto que se desea eliminar
  eliminarProducto(id: number): void {
    this.eliminar.emit(id);
  }

  // Emite el producto cuyo estado de disponibilidad se desea alternar
  onToggleDisponible(producto: Producto): void {
    this.toggleDisponible.emit(producto);
  }

}
