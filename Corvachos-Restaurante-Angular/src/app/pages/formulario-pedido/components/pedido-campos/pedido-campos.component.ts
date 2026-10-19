import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Pedido } from '../../../../models/pedido.model';
import { Cliente } from '../../../../models/cliente.model';
import { Operador } from '../../../../models/operador.model';
import { Domiciliario } from '../../../../models/domiciliario.model';

import { Producto } from '../../../../models/producto.model';
import { Adicional } from '../../../../models/adicional.model';

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
  @Input() adicionales: Adicional[] = [];
  @Input() comidasSeleccionadas: { producto: Producto; cantidad: number; adicional?: Adicional | null }[] = [];
  @Input() editando: boolean = false;

  readonly estados: string[] = ['Recibido', 'Cocinando', 'Enviado', 'Entregado', 'Cancelado'];

  productoSeleccionadoId: number = 0;
  adicionalSeleccionadoId: number = 0;
  cantidadSeleccionada: number = 1;

  get adicionalesDisponibles(): Adicional[] {
    if (!this.productoSeleccionadoId || Number(this.productoSeleccionadoId) === 0) {
      return this.adicionales.filter(a => a.disponible);
    }
    const prod = this.productos.find(p => p.id === Number(this.productoSeleccionadoId));
    if (prod && prod.categoria && prod.categoria.id) {
      const filtrados = this.adicionales.filter(a => a.disponible && a.categoria && a.categoria.id === prod.categoria.id);
      return filtrados.length > 0 ? filtrados : this.adicionales.filter(a => a.disponible);
    }
    return this.adicionales.filter(a => a.disponible);
  }

  agregarComida(): void {
    if (!this.productoSeleccionadoId || Number(this.productoSeleccionadoId) === 0) return;
    const prod = this.productos.find(p => p.id === Number(this.productoSeleccionadoId));
    if (!prod) return;

    const cant = this.cantidadSeleccionada > 0 ? this.cantidadSeleccionada : 1;
    const adic = this.adicionalSeleccionadoId && Number(this.adicionalSeleccionadoId) > 0
      ? this.adicionales.find(a => a.id_adicional === Number(this.adicionalSeleccionadoId)) ?? null
      : null;

    const existente = this.comidasSeleccionadas.find(item =>
      item.producto.id === prod.id &&
      ((!item.adicional && !adic) || (item.adicional && adic && item.adicional.id_adicional === adic.id_adicional))
    );

    if (existente) {
      existente.cantidad += cant;
    } else {
      this.comidasSeleccionadas.push({ producto: prod, cantidad: cant, adicional: adic });
    }

    this.productoSeleccionadoId = 0;
    this.adicionalSeleccionadoId = 0;
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
    return this.comidasSeleccionadas.reduce((acc, item) => {
      const precioAdicional = item.adicional ? item.adicional.precio : 0;
      return acc + ((item.producto.precio + precioAdicional) * item.cantidad);
    }, 0);
  }
}
