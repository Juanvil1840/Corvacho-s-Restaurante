import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PedidoService } from '../../service/pedido.service';
import { Pedido } from '../../models/pedido.model';
import { PedidoTablaComponent } from './components/pedido-tabla/pedido-tabla.component';

@Component({
  selector: 'app-lista-pedidos',
  standalone: true,
  imports: [CommonModule, RouterLink, PedidoTablaComponent],
  templateUrl: './lista-pedidos.component.html',
  styleUrl: './lista-pedidos.component.scss'
})
export class ListaPedidosComponent implements OnInit {

  pedidos: Pedido[] = [];
  cargando = false;
  error = '';

  constructor(private pedidoService: PedidoService) {}

  ngOnInit(): void {
    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.cargando = true;
    this.error = '';

    this.pedidoService.getPedidos().subscribe({
      next: (pedidos) => {
        this.pedidos = pedidos;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar los pedidos:', error);
        this.error = 'No fue posible cargar los pedidos. Intenta nuevamente.';
        this.cargando = false;
      }
    });
  }

  eliminarPedido(id: number): void {
    if (confirm('¿Eliminar este pedido?')) {
      this.pedidoService.eliminarPedido(id).subscribe({
        next: () => {
          this.cargarPedidos();
        },
        error: (error) => {
          console.error('Error al eliminar el pedido:', error);
          this.error = 'No fue posible eliminar el pedido.';
        }
      });
    }
  }
}
