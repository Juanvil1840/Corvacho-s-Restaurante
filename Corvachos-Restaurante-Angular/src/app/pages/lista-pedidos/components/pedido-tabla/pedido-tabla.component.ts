import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Pedido } from '../../../../models/pedido.model';

@Component({
  selector: 'app-pedido-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './pedido-tabla.component.html',
  styleUrl: './pedido-tabla.component.scss'
})
export class PedidoTablaComponent {

  @Input() pedidos: Pedido[] = [];
  @Output() eliminar = new EventEmitter<number>();

  onEliminar(id: number): void {
    this.eliminar.emit(id);
  }
}
