import { Component, Input } from '@angular/core';
import { SeguimientoPedido } from '../../../../models/seguimiento.model';
import { horaCorta, leerFecha } from '../../seguimiento-fechas';

// Tarjeta con la hora estimada de llegada del pedido (o la hora real si ya se entregó)
@Component({
  selector: 'app-pedido-llegada',
  standalone: true,
  templateUrl: './pedido-llegada.component.html',
  styleUrl: './pedido-llegada.component.scss'
})
export class PedidoLlegadaComponent {

  @Input({ required: true }) seguimiento!: SeguimientoPedido;
  // Hora actual; la página la actualiza cada pocos segundos para que la cuenta regresiva avance
  @Input({ required: true }) ahora!: Date;

  get entregado(): boolean {
    return this.seguimiento.paso === 4;
  }

  get horaLlegada(): string {
    const fecha = leerFecha(this.entregado ? this.seguimiento.fechaEntrega : this.seguimiento.llegadaEstimada);
    return fecha ? horaCorta(fecha) : '—';
  }

  // Minutos que faltan para la hora estimada (0 si ya pasó)
  get minutosRestantes(): number {
    const llegada = leerFecha(this.seguimiento.llegadaEstimada);
    return llegada ? Math.max(0, Math.ceil((llegada.getTime() - this.ahora.getTime()) / 60_000)) : 0;
  }

  // Porcentaje del tiempo estimado que ya pasó (para la barra)
  get avanceTiempo(): number {
    if (this.entregado) {
      return 100;
    }
    const creado = leerFecha(this.seguimiento.fechaCreacion);
    const llegada = leerFecha(this.seguimiento.llegadaEstimada);
    if (!creado || !llegada) {
      return 0;
    }
    const total = llegada.getTime() - creado.getTime();
    const pasado = this.ahora.getTime() - creado.getTime();
    return Math.min(100, Math.max(3, (pasado / total) * 100));
  }

  // Texto de cuánto se demoró en llegar un pedido ya entregado
  get duracionEntrega(): string {
    const creado = leerFecha(this.seguimiento.fechaCreacion);
    const entregado = leerFecha(this.seguimiento.fechaEntrega);
    if (!creado || !entregado) {
      return '';
    }
    const minutos = Math.round((entregado.getTime() - creado.getTime()) / 60_000);
    if (minutos < 1) {
      return 'menos de un minuto';
    }
    return minutos === 1 ? '1 minuto' : `${minutos} minutos`;
  }
}
