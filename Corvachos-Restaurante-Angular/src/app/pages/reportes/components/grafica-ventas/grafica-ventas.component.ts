import { AfterViewInit, Component, ElementRef, Input, OnChanges, OnDestroy, ViewChild } from '@angular/core';
import { VentaDia } from '../../../../models/reporte.model';
import { Chart, TEMA, TOOLTIP_BASE } from '../../chart-tema';
import { desdeIso, formatear } from '../../reporte-formato';

// Grafica de linea con las ventas de cada dia del periodo
@Component({
  selector: 'app-grafica-ventas',
  standalone: true,
  templateUrl: './grafica-ventas.component.html',
  styleUrl: './grafica-ventas.component.scss'
})
export class GraficaVentasComponent implements AfterViewInit, OnChanges, OnDestroy {

  @Input({ required: true }) datos: VentaDia[] = [];
  @ViewChild('lienzo') lienzo!: ElementRef<HTMLCanvasElement>;

  mejorDia: VentaDia | null = null;
  private grafica?: Chart<'line'>;

  ngAfterViewInit(): void {
    this.crear();
  }

  // Si cambia el periodo se actualizan los datos de la grafica existente (sin recrearla)
  ngOnChanges(): void {
    this.mejorDia = this.datos.reduce<VentaDia | null>((mejor, d) => (!mejor || d.ventas > mejor.ventas ? d : mejor), null);
    if (!this.grafica) {
      return;
    }
    this.grafica.data.labels = this.etiquetas();
    this.grafica.data.datasets[0].data = this.datos.map(d => d.ventas);
    this.grafica.data.datasets[0].pointRadius = this.radioPuntos();
    this.grafica.update();
  }

  ngOnDestroy(): void {
    this.grafica?.destroy();
  }

  etiquetaDia(iso: string): string {
    const texto = desdeIso(iso).toLocaleDateString('es-CO', { weekday: 'long', day: 'numeric', month: 'long' });
    return texto.charAt(0).toUpperCase() + texto.slice(1);
  }

  private crear(): void {
    const ctx = this.lienzo.nativeElement.getContext('2d');
    if (!ctx) {
      return;
    }

    // Degradado dorado bajo la linea
    const degradado = ctx.createLinearGradient(0, 0, 0, 300);
    degradado.addColorStop(0, 'rgba(205, 158, 70, 0.35)');
    degradado.addColorStop(1, 'rgba(205, 158, 70, 0)');

    this.grafica = new Chart(ctx, {
      type: 'line',
      data: {
        labels: this.etiquetas(),
        datasets: [{
          label: 'Ventas',
          data: this.datos.map(d => d.ventas),
          borderColor: TEMA.oro,
          backgroundColor: degradado,
          borderWidth: 2,
          fill: true,
          tension: 0.35,
          pointRadius: this.radioPuntos(),
          pointHoverRadius: 6,
          pointBackgroundColor: TEMA.oro,
          pointBorderColor: TEMA.tooltipFondo,
          pointBorderWidth: 2
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        plugins: {
          legend: { display: false },
          tooltip: {
            ...TOOLTIP_BASE,
            callbacks: {
              title: items => this.etiquetaDia(this.datos[items[0].dataIndex].fecha),
              label: item => `Ventas: ${formatear(item.parsed.y ?? 0, 'moneda')}`,
              afterLabel: item => `Pedidos: ${this.datos[item.dataIndex].pedidos}`
            }
          }
        },
        scales: {
          x: { grid: { display: false }, ticks: { maxRotation: 0, autoSkipPadding: 16 } },
          y: {
            beginAtZero: true,
            border: { display: false },
            grid: { color: TEMA.rejilla },
            ticks: { maxTicksLimit: 5, callback: valor => formatear(Number(valor), 'compacta') }
          }
        }
      }
    });
  }

  private etiquetas(): string[] {
    return this.datos.map(d => desdeIso(d.fecha).toLocaleDateString('es-CO', { day: 'numeric', month: 'short' }));
  }

  // Con muchos dias los puntos ensucian la linea; solo se muestran al pasar el mouse
  private radioPuntos(): number {
    return this.datos.length > 31 ? 0 : 3;
  }
}
