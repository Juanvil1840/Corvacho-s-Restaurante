import { Component, Input, OnInit, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { PedidoHistorial } from '../../../../models/pedidoHistorial.model';
import { PedidoService } from '../../../../service/pedido.service';
import { CarritoService } from '../../../../service/carrito.service';

@Component({
  selector: 'app-perfil-pedidos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './perfil-pedidos.component.html',
  styleUrl: './perfil-pedidos.component.scss'
})
export class PerfilPedidosComponent implements OnInit, OnChanges, OnDestroy {

  @Input() clienteId!: number;

  // El backend ya envía total, subtotales y adicionales listos para mostrar.
  pedidos: PedidoHistorial[] = [];
  private sub?: Subscription;

  constructor(
    private pedidoService: PedidoService,
    private carritoService: CarritoService
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
}
