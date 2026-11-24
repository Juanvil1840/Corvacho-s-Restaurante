import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { interval, takeWhile } from 'rxjs';
import { PedidoService } from '../../service/pedido.service';
import { AuthService } from '../../service/auth.service';
import { SeguimientoService } from '../../service/seguimiento.service';
import { Pedido } from '../../models/pedido.model';
import { AdicionalPedido, DetallePedido } from '../../models/detallePedido.model';
import { ESTADOS_PEDIDO } from '../../models/estado-pedido.model';
import { EventoSeguimiento, SeguimientoPedido } from '../../models/seguimiento.model';

import { PedidoCabeceraComponent } from './components/pedido-cabecera/pedido-cabecera.component';
import { PedidoProductosComponent } from './components/pedido-productos/pedido-productos.component';
import { PedidoLlegadaComponent } from './components/pedido-llegada/pedido-llegada.component';

interface Aviso {
  icono: string;
  texto: string;
}

// Cada cuánto se actualiza la cuenta regresiva de la hora de llegada
const RELOJ_MS = 15_000;
// Cuánto tiempo se muestra el aviso de cambio de estado
const DURACION_AVISO_MS = 6_000;

// Página que muestra la información detallada, el progreso de estado y los productos de un pedido.
// Mientras el pedido no se haya entregado, se actualiza en vivo cuando el restaurante cambia su estado.
@Component({
  selector: 'app-detalle-pedido',
  standalone: true,
  imports: [CommonModule, PedidoCabeceraComponent, PedidoProductosComponent, PedidoLlegadaComponent],
  templateUrl: './detalle-pedido.component.html',
  styleUrl: './detalle-pedido.component.scss'
})
export class DetallePedidoComponent implements OnInit {

  // Datos del pedido cargado y su lista de productos
  pedido: Pedido | null = null;
  detalles: DetallePedido[] = [];
  // Estado de carga y mensaje de error en caso de fallo
  cargando = true;
  error = '';

  // Seguimiento en vivo: hora estimada y aviso cuando el pedido avanza
  seguimiento: SeguimientoPedido | null = null;
  ahora = new Date();
  aviso: Aviso | null = null;
  private temporizadorAviso?: ReturnType<typeof setTimeout>;

  private seguimientoService = inject(SeguimientoService);
  private authService = inject(AuthService);
  private destroyRef = inject(DestroyRef);

  constructor(
    private pedidoService: PedidoService,
    private route: ActivatedRoute,
    private location: Location
  ) {}

  // Obtiene el ID de la ruta y consulta la información completa del pedido al iniciar
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
        // Un pedido entregado ya no cambia, así que no hace falta seguirlo
        if (pedido.estado !== 'Entregado') {
          this.seguirEnVivo(pedido.id_Pedido);
        }
      },
      error: (err) => {
        console.error('Error al cargar el pedido:', err);
        this.error = 'No fue posible encontrar el pedido solicitado.';
        this.cargando = false;
      }
    });
  }

  // Abre el canal en vivo; cuando llega "Entregado" ya no hay más cambios y se cierra solo
  private seguirEnVivo(pedidoId: number): void {
    this.seguimientoService.seguir(pedidoId).pipe(
      takeWhile(evento => !(evento.tipo === 'estado' && evento.datos.paso === 4), true),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: evento => this.procesarEvento(evento),
      // Si no se puede seguir en vivo, la página sigue funcionando como antes
      error: () => (this.seguimiento = null)
    });

    interval(RELOJ_MS).pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => (this.ahora = new Date()));
    this.destroyRef.onDestroy(() => clearTimeout(this.temporizadorAviso));
  }

  private procesarEvento(evento: EventoSeguimiento): void {
    // Los eventos de conexión (reconectando, etc.) no se muestran en esta página
    if (evento.tipo === 'conexion') {
      return;
    }
    const anterior = this.seguimiento;
    this.seguimiento = evento.datos;
    this.ahora = new Date();
    if (!this.pedido || evento.datos.estado === this.pedido.estado) {
      return;
    }
    // El estado cambia al instante; luego se recarga el pedido para traer domiciliario y fecha de entrega
    this.pedido = { ...this.pedido, estado: evento.datos.estado };
    this.pedidoService.getPedidoById(this.pedido.id_Pedido)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(pedido => {
        this.pedido = pedido;
        this.detalles = pedido.detallePedidos;
      });
    // Solo se avisa cuando el pedido avanza mientras se está mirando (no al abrir la página)
    if (anterior && evento.datos.paso > anterior.paso) {
      this.mostrarAviso(evento.datos);
    }
  }

  private mostrarAviso(datos: SeguimientoPedido): void {
    const esCliente = this.authService.getRol() === 'CLIENTE';
    const avisos: Record<number, Aviso> = esCliente
      ? {
          2: { icono: 'bi-fire', texto: '¡Tu pedido ya está en la cocina!' },
          3: { icono: 'bi-truck', texto: `¡Tu pedido va en camino!${datos.domiciliario ? ' Lo lleva ' + datos.domiciliario + '.' : ''}` },
          4: { icono: 'bi-emoji-smile', texto: '¡Tu pedido fue entregado! Buen provecho.' }
        }
      : {
          2: { icono: 'bi-fire', texto: `El pedido #${datos.pedidoId} pasó a cocina.` },
          3: { icono: 'bi-truck', texto: `El pedido #${datos.pedidoId} salió${datos.domiciliario ? ' con ' + datos.domiciliario : ''}.` },
          4: { icono: 'bi-check-circle', texto: `El pedido #${datos.pedidoId} fue entregado.` }
        };
    this.aviso = avisos[datos.paso] ?? null;
    clearTimeout(this.temporizadorAviso);
    this.temporizadorAviso = setTimeout(() => (this.aviso = null), DURACION_AVISO_MS);
    this.sonar();
  }

  // Vibración en celulares y un "ding" corto; si el navegador no lo permite simplemente no suena
  private sonar(): void {
    try {
      navigator.vibrate?.(200);
      const audio = new AudioContext();
      const oscilador = audio.createOscillator();
      const volumen = audio.createGain();
      oscilador.frequency.value = 880;
      volumen.gain.setValueAtTime(0.15, audio.currentTime);
      volumen.gain.exponentialRampToValueAtTime(0.001, audio.currentTime + 0.4);
      oscilador.connect(volumen).connect(audio.destination);
      oscilador.start();
      oscilador.stop(audio.currentTime + 0.4);
      oscilador.onended = () => audio.close();
    } catch {
      // Sin sonido: no es importante
    }
  }

  // Regresa a la pantalla anterior en el historial de navegación
  volver(): void {
    this.location.back();
  }

  // Numero del estado actual: 1 = Recibido ... 4 = Entregado
  private numeroEstadoActual(): number {
    const posicion = ESTADOS_PEDIDO.indexOf(this.pedido?.estado || '');
    return posicion >= 0 ? posicion + 1 : 1;
  }

  // Determina la clase visual de cada paso en la barra de progreso del pedido
  pasoEstado(paso: number): 'activo' | 'completado' | '' {
    const actual = this.numeroEstadoActual();
    if (paso === actual) return 'activo';
    if (paso < actual) return 'completado';
    return '';
  }

  // Indica si la línea conectora entre dos pasos ya fue superada
  lineaCompletada(despuesDePaso: number): boolean {
    return this.numeroEstadoActual() > despuesDePaso;
  }

  // Devuelve la lista de adicionales asociados a una línea del pedido
  getAdicionales(detalle: DetallePedido): AdicionalPedido[] {
    return detalle.adicionales || [];
  }

  // Calcula el subtotal de una línea sumando el producto y sus adicionales por la cantidad
  calcularSubtotal(detalle: DetallePedido): number {
    let precioUnitario = detalle.producto.precio;
    for (const adicional of this.getAdicionales(detalle)) {
      precioUnitario += adicional.precio * adicional.cantidad;
    }
    return precioUnitario * detalle.cantidad;
  }

  // Calcula el valor total del pedido sumando los subtotales de cada línea
  calcularTotal(): number {
    let total = 0;
    for (const detalle of this.detalles) {
      total += this.calcularSubtotal(detalle);
    }
    return total;
  }
}
