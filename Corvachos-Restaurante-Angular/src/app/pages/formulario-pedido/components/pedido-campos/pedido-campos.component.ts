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
  @Input() estadoOriginal: string = 'Recibido';
  @Input() domiciliarioAsignadoId: number = 0;

  private readonly ordenEstados: Record<string, number> = {
    'Recibido': 1,
    'Cocinando': 2,
    'Enviado': 3,
    'Entregado': 4
  };

  private readonly mapaSiguienteEstado: Record<string, string | null> = {
    'Recibido': 'Cocinando',
    'Cocinando': 'Enviado',
    'Enviado': 'Entregado',
    'Entregado': null
  };

  productoSeleccionadoId: number = 0;
  adicionalSeleccionadoId: number = 0;
  cantidadSeleccionada: number = 1;

  // Calcula únicamente el estado inmediatamente siguiente a partir del estado guardado en base de datos
  get siguienteEstado(): string | null {
    if (!this.editando) {
      return null;
    }
    const base = this.normalizarEstado(this.estadoOriginal || this.pedido?.estado || 'Recibido');
    return this.mapaSiguienteEstado[base] ?? null;
  }

  // Verifica si el usuario ya pulsó el botón para pasar al siguiente estado en la sesión actual de edición
  get haAvanzadoEstado(): boolean {
    if (!this.editando || !this.siguienteEstado) {
      return false;
    }
    return this.normalizarEstado(this.pedido?.estado) === this.siguienteEstado;
  }

  // Avanza el pedido exactamente un paso al siguiente estado secuencial
  avanzarAlSiguienteEstado(): void {
    const siguiente = this.siguienteEstado;
    if (siguiente) {
      this.pedido.estado = siguiente;
    }
  }

  // Permite deshacer el clic de avance antes de guardar, manteniendo el estado con el que cargó el pedido
  deshacerAvance(): void {
    if (this.editando && this.estadoOriginal) {
      this.pedido.estado = this.normalizarEstado(this.estadoOriginal);
    }
  }

  // Determina el estado visual de cada círculo en la barra de progreso (Recibido -> Cocinando -> Enviado -> Entregado)
  pasoEstado(paso: number): 'activo' | 'completado' | '' {
    const estadoActual = this.normalizarEstado(this.pedido?.estado || 'Recibido');
    const actual = this.ordenEstados[estadoActual] || 1;
    if (paso === actual) return 'activo';
    if (paso < actual) return 'completado';
    return '';
  }

  // Determina si la línea conectora entre dos pasos debe mostrarse completada
  lineaCompletada(despuesDePaso: number): boolean {
    const estadoActual = this.normalizarEstado(this.pedido?.estado || 'Recibido');
    const actual = this.ordenEstados[estadoActual] || 1;
    return actual > despuesDePaso;
  }

  normalizarEstado(texto: string): string {
    if (!texto) return 'Recibido';
    const t = texto.trim().toLowerCase();
    if (t === 'recibido' || t === 'pendiente') return 'Recibido';
    if (t === 'cocinando') return 'Cocinando';
    if (t === 'enviado') return 'Enviado';
    if (t === 'entregado') return 'Entregado';
    if (t === 'cancelado') return 'Cancelado';
    return texto.trim();
  }

  // Solo se pueden elegir domiciliarios disponibles (o el que ya tiene asignado el pedido)
  get domiciliariosDisponibles(): Domiciliario[] {
    return this.domiciliarios.filter(d => d.informaciondisp || d.id_domiciliario === this.domiciliarioAsignadoId);
  }

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
