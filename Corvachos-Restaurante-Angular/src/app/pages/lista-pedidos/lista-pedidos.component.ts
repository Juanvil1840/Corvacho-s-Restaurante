import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PedidoService } from '../../service/pedido.service';
import { PedidoLista } from '../../models/pedido.model';
import { PedidoTablaComponent } from './components/pedido-tabla/pedido-tabla.component';
import { AlertaService } from '../../service/alerta.service';

// Componente para listar y gestionar los pedidos registrados en el restaurante
@Component({
  selector: 'app-lista-pedidos',
  standalone: true,
  imports: [CommonModule, RouterLink, PedidoTablaComponent],
  templateUrl: './lista-pedidos.component.html',
  styleUrl: './lista-pedidos.component.scss'
})
export class ListaPedidosComponent implements OnInit {

  // Lista de pedidos y estados de carga y error
  pedidos: PedidoLista[] = [];
  cargando = false;
  error = '';

  // Detectar si la sesión corresponde a un operario
  get esOperario(): boolean {
    return localStorage.getItem('operador') !== null;
  }

  constructor(
    private pedidoService: PedidoService,
    private alertaService: AlertaService
  ) {}

  // Carga los pedidos al iniciar el componente
  ngOnInit(): void {
    this.cargarPedidos();
  }

  // Obtiene los pedidos desde el backend según el rol activo
  cargarPedidos(): void {
    this.cargando = true;
    this.error = '';

    // El operario solo ve los pedidos que no se han entregado
    this.pedidoService.getPedidos(this.esOperario).subscribe({
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

  // Confirma y elimina un pedido por su ID
  async eliminarPedido(id: number): Promise<void> {
    const confirmado = await this.alertaService.confirmar(
      '¿Estás seguro de que deseas eliminar este pedido?',
      'Eliminar Pedido'
    );

    if (!confirmado) return;

    this.error = '';

    this.pedidoService.eliminarPedido(id).subscribe({
      next: () => {
        this.alertaService.exito('El pedido ha sido eliminado correctamente.');
        this.cargarPedidos();
      },
      error: (error) => {
        console.error('Error al eliminar el pedido:', error);
        const mensajeError = error.error?.mensaje || 'No fue posible eliminar el pedido.';
        this.alertaService.error(mensajeError);
      }
    });
  }
}