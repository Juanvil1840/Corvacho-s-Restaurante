import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { PedidoService } from '../../service/pedido.service';
import { Pedido } from '../../models/pedido.model';

@Component({
  selector: 'app-portal-operario',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './portal-operario.component.html',
  styleUrl: './portal-operario.component.scss'
})
export class PortalOperarioComponent implements OnInit {

  pedidos: Pedido[] = [];
  cargando = false;
  error = '';
  nombreOperador = '';

  constructor(
    private pedidoService: PedidoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // 🛡️ Protección: si no hay sesión, al login
    const opStr = localStorage.getItem('operador');
    if (!opStr) {
      this.router.navigate(['/login-operario']);
      return;
    }

    this.nombreOperador = JSON.parse(opStr).nombre;
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
        this.error = 'No fue posible cargar los pedidos.';
        this.cargando = false;
      }
    });
  }

  cerrarSesion(): void {
    localStorage.removeItem('operador');
    this.router.navigate(['/login-operario']);
  }
}