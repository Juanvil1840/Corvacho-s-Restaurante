import { AfterViewInit, Component, ElementRef, Input, OnChanges, OnDestroy, ViewChild } from '@angular/core';
import { Ranking } from '../../../../models/reporte.model';
import { Chart, TEMA, TOOLTIP_BASE } from '../../chart-tema';
import { formatear } from '../../reporte-formato';

// Barras horizontales con lo que vendio cada categoria del menu (ordenadas de mayor a menor)
@Component({
  selector: 'app-grafica-categorias',
  standalone: true,
  templateUrl: './grafica-categorias.component.html',
  styleUrl: './grafica-categorias.component.scss'
})
export class GraficaCategoriasComponent implements AfterViewInit, OnChanges, OnDestroy {

  @Input({ required: true }) datos: Ranking[] = [];
  @ViewChild('lienzo') lienzo!: ElementRef<HTMLCanvasElement>;

  private grafica?: Chart<'bar'>;

  get total(): number {
    return this.datos.reduce((suma, c) => suma + c.total, 0);
  }

  ngAfterViewInit(): void {
    this.crear();
  }

  ngOnChanges(): void {
    if (!this.grafica) {
      return;
    }
    this.grafica.data.labels = this.datos.map(c => c.nombre);
    this.grafica.data.datasets[0].data = this.datos.map(c => c.total);
    this.grafica.update();
  }

  ngOnDestroy(): void {
    this.grafica?.destroy();
  }

  private crear(): void {
    this.grafica = new Chart(this.lienzo.nativeElement, {
      type: 'bar',
      data: {
        labels: this.datos.map(c => c.nombre),
        datasets: [{
          label: 'Ventas',
          data: this.datos.map(c => c.total),
          backgroundColor: TEMA.oro,
          hoverBackgroundColor: '#e2b765',
          borderRadius: 4,
          borderSkipped: 'start',
          barPercentage: 0.7,
          categoryPercentage: 0.9
        }]
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            ...TOOLTIP_BASE,
            callbacks: {
              label: item => `Ventas: ${formatear(item.parsed.x ?? 0, 'moneda')}`,
              afterLabel: item => {
                const categoria = this.datos[item.dataIndex];
                const porcentaje = this.total ? (categoria.total / this.total) * 100 : 0;
                return [`Unidades: ${categoria.cantidad}`, `${formatear(porcentaje, 'decimal')}% del total`];
              }
            }
          }
        },
        scales: {
          x: {
            beginAtZero: true,
            border: { display: false },
            grid: { color: TEMA.rejilla },
            ticks: { maxTicksLimit: 5, callback: valor => formatear(Number(valor), 'compacta') }
          },
          y: { grid: { display: false }, border: { display: false }, ticks: { color: TEMA.texto } }
        }
      }
    });
  }
}
