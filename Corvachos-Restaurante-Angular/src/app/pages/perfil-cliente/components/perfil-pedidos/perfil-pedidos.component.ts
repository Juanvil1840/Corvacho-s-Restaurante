import { Component, Input, OnInit, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { Pedido } from '../../../../models/pedido.model';
import { AdicionalSeleccionadoItem, DetallePedido } from '../../../../models/detallePedido.model';
import { PedidoService } from '../../../../service/pedido.service';
import { CarritoServiceService } from '../../../../service/carrito-service.service';

@Component({
  selector: 'app-perfil-pedidos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './perfil-pedidos.component.html',
  styleUrl: './perfil-pedidos.component.scss'
})
export class PerfilPedidosComponent implements OnInit, OnChanges, OnDestroy {

  @Input() clienteId!: number;

  pedidos: Pedido[] = [];
  private sub?: Subscription;

  constructor(
    private pedidoService: PedidoService,
    private carritoService: CarritoServiceService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
    this.sub = this.carritoService.pedidoCreado$.subscribe(() => {
      this.cargarDatos();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['clienteId'] && !changes['clienteId'].firstChange) {
      this.cargarDatos();
    }
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  cargarDatos(): void {
    if (!this.clienteId || this.clienteId <= 0) return;

    this.pedidoService.getPedidosPorCliente(this.clienteId).subscribe({
      next: (pedidos) => {
        this.pedidos = (pedidos || []).sort((a, b) => (b.id_Pedido || 0) - (a.id_Pedido || 0));
      },
      error: (err) => console.error('Error al cargar pedidos del cliente:', err)
    });
  }

  getDetallesDelPedido(pedido: Pedido): DetallePedido[] {
    return (pedido.detallePedidos as DetallePedido[]) || [];
  }

  getAdicionalesDeDetalle(detalle: DetallePedido): AdicionalSeleccionadoItem[] {
    if (detalle.adicionales && detalle.adicionales.length > 0) {
      return detalle.adicionales;
    }
    if (detalle.adicional) {
      return [{
        id_adicional: detalle.adicional.id_adicional,
        nombre: detalle.adicional.nombre,
        precio: detalle.adicional.precio || 0,
        cantidad: 1
      }];
    }
    return [];
  }

  calcularSubtotalDetalle(detalle: DetallePedido): number {
    const base = detalle.producto?.precio || 0;
    const extras = this.getAdicionalesDeDetalle(detalle).reduce(
      (sum, a) => sum + (a.precio || 0) * (a.cantidad || 1),
      0
    );
    return (base + extras) * (detalle.cantidad || 1);
  }

  calcularTotal(pedido: Pedido): number {
    return this.getDetallesDelPedido(pedido).reduce(
      (total, d) => total + this.calcularSubtotalDetalle(d),
      0
    );
  }
}
