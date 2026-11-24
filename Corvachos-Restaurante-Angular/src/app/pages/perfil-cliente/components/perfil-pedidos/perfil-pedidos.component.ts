import { Component, Input, OnInit, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { PedidoHistorial } from '../../../../models/pedidoHistorial.model';
import { PedidoService } from '../../../../service/pedido.service';
import { CarritoService } from '../../../../service/carrito.service';
import { ResenaService } from '../../../../service/resena.service';
import { Resena } from '../../../../models/resena.model';
import { CalificacionProducto } from '../../../../models/calificacionProducto.model';
import { CalificacionProductoService } from '../../../../service/calificacion-producto.service';

// Componente que muestra el historial de pedidos realizados por un cliente
@Component({
  selector: 'app-perfil-pedidos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './perfil-pedidos.component.html',
  styleUrl: './perfil-pedidos.component.scss'
})
export class PerfilPedidosComponent implements OnInit, OnChanges, OnDestroy {

  // ID del cliente cuyos pedidos se van a consultar
  @Input() clienteId!: number;

  // El backend ya envía total, subtotales y adicionales listos para mostrar.
  pedidos: PedidoHistorial[] = [];
  private sub?: Subscription;
  resenas = new Map<number, Resena>();
  puntuaciones: Record<number, number> = {};
  comentarios: Record<number, string> = {};
  guardando = new Set<number>();
  errores: Record<number, string> = {};
  calificacionesProducto = new Map<string, CalificacionProducto>();
  puntuacionesProducto: Record<string, number> = {};
  guardandoProducto = new Set<string>();
  erroresProducto: Record<string, string> = {};

  constructor(
    private pedidoService: PedidoService,
    private carritoService: CarritoService,
    private resenaService: ResenaService,
    private calificacionProductoService: CalificacionProductoService
  ) {}

  // Carga el historial inicial y se suscribe a nuevos pedidos creados desde el carrito
  ngOnInit(): void {
    this.cargarDatos();
    this.sub = this.carritoService.pedidoCreado$.subscribe(() => {
      this.cargarDatos();
    });
  }

  // Recarga el historial si cambia el ID del cliente recibido por Input
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['clienteId'] && !changes['clienteId'].firstChange) {
      this.cargarDatos();
    }
  }

  // Cancela la suscripción al destruir el componente para evitar fugas de memoria
  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  // Consulta los pedidos del cliente y los ordena del más reciente al más antiguo
  cargarDatos(): void {
    if (!this.clienteId || this.clienteId <= 0) return;

    this.pedidoService.getPedidosPorCliente(this.clienteId).subscribe({
      next: (pedidos) => {
        this.pedidos = (pedidos || []).sort((a, b) => (b.id_Pedido || 0) - (a.id_Pedido || 0));
        this.cargarResenas();
        this.cargarCalificacionesProducto();
      },
      error: (err) => console.error('Error al cargar pedidos del cliente:', err)
    });
  }

  private cargarResenas(): void {
    this.resenaService.getPorCliente(this.clienteId).subscribe({
      next: resenas => this.resenas = new Map(resenas.map(resena => [resena.pedidoId, resena])),
      error: err => console.error('Error al cargar las reseñas:', err)
    });
  }

  private cargarCalificacionesProducto(): void {
    this.calificacionProductoService.getPorCliente(this.clienteId).subscribe({
      next: calificaciones => this.calificacionesProducto = new Map(
        calificaciones.map(calificacion => [this.claveCalificacion(calificacion.pedidoId, calificacion.productoId), calificacion])
      ),
      error: err => console.error('Error al cargar las calificaciones de productos:', err)
    });
  }

  claveCalificacion(pedidoId: number, productoId: number): string {
    return `${pedidoId}-${productoId}`;
  }

  calificarProducto(pedidoId: number, productoId: number): void {
    const clave = this.claveCalificacion(pedidoId, productoId);
    const puntuacion = this.puntuacionesProducto[clave];
    if (!puntuacion || this.guardandoProducto.has(clave)) return;
    this.guardandoProducto.add(clave);
    this.erroresProducto[clave] = '';
    this.calificacionProductoService.calificar(pedidoId, productoId, puntuacion).subscribe({
      next: calificacion => {
        this.calificacionesProducto.set(clave, calificacion);
        this.calificacionesProducto = new Map(this.calificacionesProducto);
        this.guardandoProducto.delete(clave);
      },
      error: err => {
        this.erroresProducto[clave] = err.status === 409
          ? 'Ya calificaste este producto en este pedido.'
          : 'No fue posible guardar la calificación. Intenta nuevamente.';
        this.guardandoProducto.delete(clave);
      }
    });
  }

  calificar(pedidoId: number): void {
    const puntuacion = this.puntuaciones[pedidoId];
    if (!puntuacion || this.guardando.has(pedidoId)) return;
    this.guardando.add(pedidoId);
    this.errores[pedidoId] = '';
    this.resenaService.crear(pedidoId, {
      puntuacion,
      comentario: this.comentarios[pedidoId]?.trim() || undefined
    }).subscribe({
      next: resena => {
        this.resenas.set(pedidoId, resena);
        this.resenas = new Map(this.resenas);
        this.guardando.delete(pedidoId);
      },
      error: err => {
        this.errores[pedidoId] = err.status === 409
          ? 'Este pedido ya tiene una calificación.'
          : 'No fue posible guardar la reseña. Intenta nuevamente.';
        this.guardando.delete(pedidoId);
      }
    });
  }
}
