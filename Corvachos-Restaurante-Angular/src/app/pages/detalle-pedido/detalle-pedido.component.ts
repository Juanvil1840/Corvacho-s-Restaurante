import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { PedidoService } from '../../service/pedido.service';
import { Pedido } from '../../models/pedido.model';
import { AdicionalPedido, DetallePedido } from '../../models/detallePedido.model';
import { ESTADOS_PEDIDO } from '../../models/estado-pedido.model';

@Component({
  selector: 'app-detalle-pedido',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detalle-pedido.component.html',
  styleUrl: './detalle-pedido.component.scss'
})
export class DetallePedidoComponent implements OnInit {

  pedido: Pedido | null = null;
  detalles: DetallePedido[] = [];
  cargando = true;
  error = '';

  constructor(
    private pedidoService: PedidoService,
    private route: ActivatedRoute,
    private location: Location
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      this.error = 'No se especificó el identificador del pedido.';
      this.cargando = false;
      return;
    }

    this.pedidoService.getPedidoById(Number(idParam)).subscribe({
      next: (pedido) => {
        this.pedido = pedido;
        this.detalles = pedido.detallePedidos;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar el pedido:', err);
        this.error = 'No fue posible encontrar el pedido solicitado.';
        this.cargando = false;
      }
    });
  }

  volver(): void {
    this.location.back();
  }

  // Numero del estado actual: 1 = Recibido ... 4 = Entregado
  private numeroEstadoActual(): number {
    const posicion = ESTADOS_PEDIDO.indexOf(this.pedido?.estado || '');
    return posicion >= 0 ? posicion + 1 : 1;
  }

  pasoEstado(paso: number): 'activo' | 'completado' | '' {
    const actual = this.numeroEstadoActual();
    if (paso === actual) return 'activo';
    if (paso < actual) return 'completado';
    return '';
  }

  lineaCompletada(despuesDePaso: number): boolean {
    return this.numeroEstadoActual() > despuesDePaso;
  }

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
