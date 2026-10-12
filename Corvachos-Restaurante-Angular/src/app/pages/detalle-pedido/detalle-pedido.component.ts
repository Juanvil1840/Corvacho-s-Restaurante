import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { PedidoService } from '../../service/pedido.service';
import { DetallePedidoServiceService } from '../../service/detalle-pedido-service.service';
import { Pedido } from '../../models/pedido.model';
import { DetallePedido } from '../../models/detallePedido.model';

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
    private detallePedidoService: DetallePedidoServiceService,
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

    const id = Number(idParam);
    this.pedidoService.getPedidoById(id).subscribe({
      next: (pedido) => {
        this.pedido = pedido;
        this.detallePedidoService.obtenerPorPedido(id).subscribe({
          next: (detalles) => {
            this.detalles = detalles;
            this.cargando = false;
          },
          error: (err) => {
            console.error('Error al cargar detalles del pedido:', err);
            this.detalles = [];
            this.cargando = false;
          }
        });
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

  esEstado(estado: string): boolean {
    return this.pedido?.estado === estado;
  }

  pasoEstado(paso: number): 'activo' | 'completado' | '' {
    const orden: Record<string, number> = {
      'Recibido': 1,
      'Cocinando': 2,
      'Enviado': 3,
      'Entregado': 4
    };
    const actual = orden[this.pedido?.estado || ''] || 1;
    if (paso === actual) return 'activo';
    if (paso < actual) return 'completado';
    return '';
  }

  lineaCompletada(despuesDePaso: number): boolean {
    const orden: Record<string, number> = {
      'Recibido': 1,
      'Cocinando': 2,
      'Enviado': 3,
      'Entregado': 4
    };
    const actual = orden[this.pedido?.estado || ''] || 1;
    return actual > despuesDePaso;
  }

  calcularSubtotal(detalle: DetallePedido): number {
    const precioProducto = detalle.producto?.precio || 0;
    const precioAdicional = detalle.adicional?.precio || 0;
    return (precioProducto + precioAdicional) * detalle.cantidad;
  }

  calcularTotal(): number {
    return this.detalles.reduce((acc, d) => acc + this.calcularSubtotal(d), 0);
  }
}
