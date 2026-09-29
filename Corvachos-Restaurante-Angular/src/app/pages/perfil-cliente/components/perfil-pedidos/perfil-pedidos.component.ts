import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Pedido } from '../../../../models/pedido.model';
import { DetallePedido } from '../../../../models/detallePedido.model';
import { PedidoServiceService } from '../../../../service/pedido-service.service';
import { DetallePedidoServiceService } from '../../../../service/detalle-pedido-service.service';

@Component({
  selector: 'app-perfil-pedidos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './perfil-pedidos.component.html',
  styleUrl: './perfil-pedidos.component.scss'
})
export class PerfilPedidosComponent implements OnInit {

  @Input() clienteId!: number;

  pedidos: Pedido[] = [];

  constructor(
    private pedidoService: PedidoServiceService,
    private detallePedidoService: DetallePedidoServiceService
  ) {}

  ngOnInit(): void {
    this.pedidos = this.pedidoService.getPedidos().filter(
      p => p.cliente && p.cliente.clienteId === this.clienteId
    );
  }

  getDetallesDelPedido(pedidoId: number): DetallePedido[] {
    return this.detallePedidoService.getDetallesPedidos().filter(
      d => d.pedido && d.pedido.id_Pedido === pedidoId
    );
  }

  calcularTotal(pedidoId: number): number {
    return this.getDetallesDelPedido(pedidoId).reduce(
      (total, d) => total + (d.producto.precio * d.cantidad),
      0
    );
  }
}