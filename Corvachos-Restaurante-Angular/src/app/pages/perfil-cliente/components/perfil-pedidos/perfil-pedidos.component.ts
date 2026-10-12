import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Pedido } from '../../../../models/pedido.model';
import { DetallePedido } from '../../../../models/detallePedido.model';
import { PedidoService } from '../../../../service/pedido.service';
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
  detalles: DetallePedido[] = [];

  constructor(
    private pedidoService: PedidoService,
    private detallePedidoService: DetallePedidoServiceService
  ) {}

  ngOnInit(): void {
    this.pedidoService.getPedidos().subscribe({
      next: (pedidos) => {
        this.pedidos = pedidos.filter(
          p => p.cliente && p.cliente.clienteId === this.clienteId
        );
      },
      error: (err) => console.error('Error al cargar pedidos del cliente:', err)
    });

    this.detallePedidoService.obtenerTodos().subscribe({
      next: (detalles) => {
        this.detalles = detalles;
      },
      error: (err) => console.error('Error al cargar detalles de pedidos:', err)
    });
  }

  getDetallesDelPedido(pedidoId: number): DetallePedido[] {
    return this.detalles.filter(
      d => d.pedido && d.pedido.id_Pedido === pedidoId
    );
  }

  calcularTotal(pedidoId: number): number {
    return this.getDetallesDelPedido(pedidoId).reduce(
      (total, d) => total + ((d.producto.precio + (d.adicional ? d.adicional.precio : 0)) * d.cantidad),
      0
    );
  }
}