import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Pedido } from '../../../../models/pedido.model';
import { Cliente } from '../../../../models/cliente.model';
import { Operador } from '../../../../models/operador.model';
import { Domiciliario } from '../../../../models/domiciliario.model';

import { Producto } from '../../../../models/producto.model';

@Component({
  selector: 'app-pedido-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pedido-campos.component.html',
  styleUrl: './pedido-campos.component.scss'
})
export class PedidoCamposComponent {

  @Input() pedido!: Pedido;
  @Input() clientes: Cliente[] = [];
  @Input() operadores: Operador[] = [];
  @Input() domiciliarios: Domiciliario[] = [];
  @Input() productos: Producto[] = [];
  @Input() comidasSeleccionadas: { producto: Producto; cantidad: number }[] = [];

  readonly estados: string[] = ['Recibido', 'Cocinando', 'Enviado', 'Entregado', 'Cancelado'];

  productoSeleccionadoId: number = 0;
  cantidadSeleccionada: number = 1;

  agregarComida(): void {
    if (!this.productoSeleccionadoId || Number(this.productoSeleccionadoId) === 0) return;
    const prod = this.productos.find(p => p.id === Number(this.productoSeleccionadoId));
    if (!prod) return;

    const cant = this.cantidadSeleccionada > 0 ? this.cantidadSeleccionada : 1;
    const existente = this.comidasSeleccionadas.find(item => item.producto.id === prod.id);
    if (existente) {
      existente.cantidad += cant;
    } else {
      this.comidasSeleccionadas.push({ producto: prod, cantidad: cant });
    }

    this.productoSeleccionadoId = 0;
    this.cantidadSeleccionada = 1;
  }

  quitarComida(index: number): void {
    this.comidasSeleccionadas.splice(index, 1);
  }

  modificarCantidad(index: number, delta: number): void {
    const item = this.comidasSeleccionadas[index];
    if (!item) return;
    const nueva = item.cantidad + delta;
    if (nueva <= 0) {
      this.quitarComida(index);
    } else {
      item.cantidad = nueva;
    }
  }

  calcularTotal(): number {
    return this.comidasSeleccionadas.reduce((acc, item) => acc + (item.producto.precio * item.cantidad), 0);
  }
}
