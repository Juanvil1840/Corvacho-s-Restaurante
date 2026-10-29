import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdicionalPedido, DetallePedido } from '../../../../models/detallePedido.model';

@Component({
  selector: 'app-pedido-productos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedido-productos.component.html',
  styleUrl: './pedido-productos.component.scss'
})
export class PedidoProductosComponent {
  
  @Input() detalles: DetallePedido[] = [];

  getAdicionales(detalle: DetallePedido): AdicionalPedido[] {
    return detalle.adicionales || [];
  }

  calcularSubtotal(detalle: DetallePedido): number {
    let precioUnitario = detalle.producto.precio;
    for (const adicional of this.getAdicionales(detalle)) {
      precioUnitario += adicional.precio * adicional.cantidad;
    }
    return precioUnitario * detalle.cantidad;
  }

  calcularTotal(): number {
    let total = 0;
    for (const detalle of this.detalles) {
      total += this.calcularSubtotal(detalle);
    }
    return total;
  }
}