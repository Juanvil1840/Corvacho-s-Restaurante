import { Component, Input } from '@angular/core';
import { Indicadores } from '../../../../models/reporte.model';
import { ContadorDirective } from '../../contador.directive';
import { TipoFormato, formatear } from '../../reporte-formato';

interface Kpi {
  titulo: string;
  icono: string;
  valor: number;
  formato: TipoFormato;
  sufijo?: string;
  variacion?: number | null;
  nota?: string;
  enVivo?: boolean;
}

// Tarjetas con los indicadores clave del periodo y su variacion frente al periodo anterior
@Component({
  selector: 'app-reporte-kpis',
  standalone: true,
  imports: [ContadorDirective],
  templateUrl: './reporte-kpis.component.html',
  styleUrl: './reporte-kpis.component.scss'
})
export class ReporteKpisComponent {

  kpis: Kpi[] = [];

  @Input({ required: true }) set indicadores(i: Indicadores) {
    this.kpis = [
      { titulo: 'Ventas', icono: 'bi-cash-stack', valor: i.ventas, formato: 'moneda', variacion: i.variacionVentas },
      { titulo: 'Pedidos', icono: 'bi-bag-check', valor: i.pedidos, formato: 'entero', variacion: i.variacionPedidos },
      { titulo: 'Ticket promedio', icono: 'bi-receipt', valor: i.ticketPromedio, formato: 'moneda', variacion: i.variacionTicket },
      { titulo: 'Clientes únicos', icono: 'bi-people', valor: i.clientesUnicos, formato: 'entero', variacion: i.variacionClientes },
      { titulo: 'Platos vendidos', icono: 'bi-egg-fried', valor: i.unidadesVendidas, formato: 'entero', nota: 'unidades en el periodo' },
      {
        titulo: 'Ingresos por adicionales', icono: 'bi-plus-circle', valor: i.ingresosAdicionales, formato: 'moneda',
        nota: `${formatear(i.porcentajeAdicionales, 'decimal')}% de las ventas`
      },
      {
        titulo: 'Tiempo de entrega', icono: 'bi-stopwatch', valor: i.minutosPromedioEntrega ?? 0, formato: 'entero',
        sufijo: 'min', nota: i.minutosPromedioEntrega == null ? 'sin entregas en el periodo' : 'promedio, de pedido a entrega'
      },
      { titulo: 'Pedidos en curso', icono: 'bi-broadcast', valor: i.pedidosActivos, formato: 'entero', nota: 'ahora mismo', enVivo: true }
    ];
  }

  // Texto de la variacion con signo, por ejemplo "+12,3%"
  textoVariacion(variacion: number): string {
    const signo = variacion > 0 ? '+' : '';
    return `${signo}${formatear(variacion, 'decimal')}%`;
  }
}
