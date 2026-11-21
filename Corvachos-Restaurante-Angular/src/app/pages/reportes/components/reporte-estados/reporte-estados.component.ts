import { Component, Input } from '@angular/core';
import { Conteo } from '../../../../models/reporte.model';
import { formatear } from '../../reporte-formato';

// Flujo de los pedidos del periodo por estado: recibido -> cocinando -> enviado -> entregado
@Component({
  selector: 'app-reporte-estados',
  standalone: true,
  templateUrl: './reporte-estados.component.html',
  styleUrl: './reporte-estados.component.scss'
})
export class ReporteEstadosComponent {

  @Input({ required: true }) estados: Conteo[] = [];
  @Input() minutosEntrega: number | null = null;

  private readonly iconos: Record<string, string> = {
    Recibido: 'bi-receipt-cutoff',
    Cocinando: 'bi-fire',
    Enviado: 'bi-scooter',
    Entregado: 'bi-check2-circle'
  };

  get total(): number {
    return this.estados.reduce((suma, e) => suma + e.cantidad, 0);
  }

  icono(nombre: string): string {
    return this.iconos[nombre] ?? 'bi-circle';
  }

  porcentaje(cantidad: number): string {
    return this.total ? `${formatear((cantidad / this.total) * 100, 'decimal')}%` : '0%';
  }

  get minutosTexto(): string {
    return this.minutosEntrega == null ? '—' : formatear(this.minutosEntrega, 'entero');
  }
}
