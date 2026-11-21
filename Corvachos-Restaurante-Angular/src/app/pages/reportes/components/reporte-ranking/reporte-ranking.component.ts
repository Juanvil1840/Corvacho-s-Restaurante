import { Component, Input } from '@angular/core';
import { Ranking } from '../../../../models/reporte.model';
import { formatear } from '../../reporte-formato';

// Lista ordenada con barra proporcional; se reutiliza para platos, adicionales y clientes
@Component({
  selector: 'app-reporte-ranking',
  standalone: true,
  templateUrl: './reporte-ranking.component.html',
  styleUrl: './reporte-ranking.component.scss'
})
export class ReporteRankingComponent {

  @Input({ required: true }) titulo = '';
  @Input() subtitulo = '';
  @Input() icono = 'bi-trophy';
  @Input({ required: true }) items: Ranking[] = [];

  // Metrica que define el largo de la barra y el valor destacado
  @Input() metrica: 'cantidad' | 'total' = 'total';
  @Input() unidad = 'unid.';
  @Input() conImagen = false;
  @Input() textoVacio = 'Sin datos en este periodo';

  // Imagenes que no cargaron (se reemplazan por un icono)
  fallidas = new Set<string>();

  get maximo(): number {
    return Math.max(1, ...this.items.map(i => i[this.metrica]));
  }

  porcentaje(item: Ranking): number {
    return (item[this.metrica] / this.maximo) * 100;
  }

  valorPrincipal(item: Ranking): string {
    return this.metrica === 'total' ? formatear(item.total, 'moneda') : `${formatear(item.cantidad, 'entero')} ${this.unidad}`;
  }

  valorSecundario(item: Ranking): string {
    return this.metrica === 'total' ? `${formatear(item.cantidad, 'entero')} ${this.unidad}` : formatear(item.total, 'moneda');
  }

  iniciales(nombre: string): string {
    return nombre.split(' ').filter(Boolean).slice(0, 2).map(p => p[0].toUpperCase()).join('');
  }
}
