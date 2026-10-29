import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PedidoLista } from '../../../../models/pedido.model';

// Componente que muestra la tabla con el listado de pedidos
@Component({
  selector: 'app-pedido-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './pedido-tabla.component.html',
  styleUrl: './pedido-tabla.component.scss'
})
export class PedidoTablaComponent {

  // Lista de pedidos recibida desde el componente padre
  @Input() pedidos: PedidoLista[] = [];
  // Evento emitido cuando se solicita eliminar un pedido
  @Output() eliminar = new EventEmitter<number>();

  // Emite el ID del pedido a eliminar
  onEliminar(id: number): void {
    this.eliminar.emit(id);
  }
}
