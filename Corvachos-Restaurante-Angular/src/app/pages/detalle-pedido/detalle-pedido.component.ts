import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { PedidoService } from '../../service/pedido.service';
import { Pedido } from '../../models/pedido.model';
import { DetallePedido } from '../../models/detallePedido.model';

// Importamos los subcomponentes
import { PedidoCabeceraComponent } from './components/pedido-cabecera/pedido-cabecera.component';
import { PedidoProductosComponent } from './components/pedido-productos/pedido-productos.component';

@Component({
  selector: 'app-detalle-pedido',
  standalone: true,
  imports: [CommonModule, PedidoCabeceraComponent, PedidoProductosComponent],
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
}