import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Cliente } from '../../../../models/cliente.model';
import { Operador } from '../../../../models/operador.model';
import { Domiciliario } from '../../../../models/domiciliario.model';
import { Producto } from '../../../../models/producto.model';
import { Adicional } from '../../../../models/adicional.model';
import { AdicionalPedido, ProductoPedido } from '../../../../models/detallePedido.model';
import { ESTADOS_PEDIDO } from '../../../../models/estado-pedido.model';

// Datos que se llenan en el formulario (0 = no seleccionado)
export interface FormularioPedido {
  estado: string;
  fechaCreacion: string;
  fechaEntrega: string | null;
  clienteId: number;
  operadorId: number;
  domiciliarioId: number;
}

// Una comida del pedido con sus adicionales
export interface ComidaPedido {
  producto: ProductoPedido;
  cantidad: number;
  adicionales: AdicionalPedido[];
}

@Component({
  selector: 'app-pedido-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pedido-campos.component.html',
  styleUrl: './pedido-campos.component.scss'
})
export class PedidoCamposComponent {
  @Input() formulario!: FormularioPedido;
  @Input() clientes: Cliente[] = [];
  @Input() operadores: Operador[] = [];
  @Input() domiciliarios: Domiciliario[] = [];
  @Input() productos: Producto[] = [];
  @Input() adicionales: Adicional[] = [];
  @Input() comidasSeleccionadas: ComidaPedido[] = [];
  @Input() editando: boolean = false;
  @Input() estadoOriginal: string = 'Recibido';
  @Input() domiciliarioAsignadoId: number = 0;

  productoSeleccionadoId: number = 0;
  adicionalSeleccionadoId: number = 0;
  cantidadSeleccionada: number = 1;

  // El estado que sigue al estado guardado en la base de datos
  get siguienteEstado(): string | null {
    if (!this.editando) {
      return null;
    }
    const posicion = ESTADOS_PEDIDO.indexOf(this.estadoOriginal);
    if (posicion < 0 || posicion === ESTADOS_PEDIDO.length - 1) {
      return null;
    }
    return ESTADOS_PEDIDO[posicion + 1];
  }

  // true si ya se pulso el boton de avanzar y aun no se ha guardado
  get haAvanzadoEstado(): boolean {
    return this.siguienteEstado !== null && this.formulario.estado === this.siguienteEstado;
  }

  avanzarAlSiguienteEstado(): void {
    if (this.siguienteEstado) {
      this.formulario.estado = this.siguienteEstado;
    }
  }

  deshacerAvance(): void {
    this.formulario.estado = this.estadoOriginal;
  }

  // Numero del estado actual: 1 = Recibido ... 4 = Entregado
  private numeroEstadoActual(): number {
    const posicion = ESTADOS_PEDIDO.indexOf(this.formulario.estado);
    return posicion >= 0 ? posicion + 1 : 1;
  }

  // Clase de cada circulo de la barra de progreso
  pasoEstado(paso: number): 'activo' | 'completado' | '' {
    const actual = this.numeroEstadoActual();
    if (paso === actual) return 'activo';
    if (paso < actual) return 'completado';
    return '';
  }

  lineaCompletada(despuesDePaso: number): boolean {
    return this.numeroEstadoActual() > despuesDePaso;
  }

  // Solo se pueden elegir domiciliarios disponibles (o el que ya tiene asignado el pedido)
  get domiciliariosDisponibles(): Domiciliario[] {
    return this.domiciliarios.filter(d => d.informaciondisp || d.id_domiciliario === this.domiciliarioAsignadoId);
  }

  // Adicionales disponibles de la categoria del producto elegido
  get adicionalesDisponibles(): Adicional[] {
    const producto = this.productos.find(p => p.id === Number(this.productoSeleccionadoId));
    if (!producto) {
      return [];
    }
    return this.adicionales.filter(a => a.disponible && a.categoria?.id === producto.categoria.id);
  }

  agregarComida(): void {
    const producto = this.productos.find(p => p.id === Number(this.productoSeleccionadoId));
    if (!producto) {
      return;
    }
    const cantidad = this.cantidadSeleccionada > 0 ? this.cantidadSeleccionada : 1;
    const adicional = this.adicionales.find(a => a.id_adicional === Number(this.adicionalSeleccionadoId));
    const adicionales: AdicionalPedido[] = adicional
      ? [{ id_adicional: adicional.id_adicional, nombre: adicional.nombre, precio: adicional.precio, cantidad: 1 }]
      : [];

    // Si ya esta el mismo producto con los mismos adicionales, solo se suma la cantidad
    const existente = this.comidasSeleccionadas.find(item =>
      item.producto.id === producto.id && this.mismosAdicionales(item.adicionales, adicionales)
    );
    if (existente) {
      existente.cantidad += cantidad;
    } else {
      this.comidasSeleccionadas.push({
        producto: {
          id: producto.id,
          nombre: producto.nombre,
          precio: producto.precio,
          descripcion: producto.descripcion,
          imagen: producto.imagen
        },
        cantidad: cantidad,
        adicionales: adicionales
      });
    }

    this.productoSeleccionadoId = 0;
    this.adicionalSeleccionadoId = 0;
    this.cantidadSeleccionada = 1;
  }

  quitarComida(index: number): void {
    this.comidasSeleccionadas.splice(index, 1);
  }

  modificarCantidad(index: number, cambio: number): void {
    const item = this.comidasSeleccionadas[index];
    if (!item) return;
    const nueva = item.cantidad + cambio;
    if (nueva <= 0) {
      this.quitarComida(index);
    } else {
      item.cantidad = nueva;
    }
  }

  // Precio de una unidad de la comida con sus adicionales
  precioUnitario(item: ComidaPedido): number {
    let precio = item.producto.precio;
    for (const adicional of item.adicionales) {
      precio += adicional.precio * adicional.cantidad;
    }
    return precio;
  }

  calcularTotal(): number {
    let total = 0;
    for (const item of this.comidasSeleccionadas) {
      total += this.precioUnitario(item) * item.cantidad;
    }
    return total;
  }

  private mismosAdicionales(a: AdicionalPedido[], b: AdicionalPedido[]): boolean {
    if (a.length !== b.length) {
      return false;
    }
    return a.every(adicionalA => b.some(adicionalB => adicionalB.id_adicional === adicionalA.id_adicional));
  }
}
