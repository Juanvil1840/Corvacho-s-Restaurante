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
  // Cupón usado en el pedido y valor descontado (el total ya lo tiene restado)
  @Input() cupon: string | null | undefined = null;
  @Input() descuento = 0;

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

  calcularSubtotalPedido(): number {
    let total = 0;
    for (const detalle of this.detalles) {
      total += this.calcularSubtotal(detalle);
    }
    return total;
  }

  calcularTotal(): number {
    return Math.max(0, this.calcularSubtotalPedido() - (this.descuento || 0));
  }
}