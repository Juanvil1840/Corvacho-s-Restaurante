import { Component, DestroyRef, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { EMPTY, Subject, catchError, finalize, map, switchMap, timer } from 'rxjs';
import { RangoFechas, ReporteDashboard } from '../../models/reporte.model';
import { ReporteService } from '../../service/reporte.service';
import { fechaLegible } from './reporte-formato';
import { ReporteFiltrosComponent } from './components/reporte-filtros/reporte-filtros.component';
import { ReporteKpisComponent } from './components/reporte-kpis/reporte-kpis.component';
import { GraficaVentasComponent } from './components/grafica-ventas/grafica-ventas.component';
import { GraficaCategoriasComponent } from './components/grafica-categorias/grafica-categorias.component';
import { ReporteRankingComponent } from './components/reporte-ranking/reporte-ranking.component';
import { ReporteEstadosComponent } from './components/reporte-estados/reporte-estados.component';
import { MapaCalorComponent } from './components/mapa-calor/mapa-calor.component';

// Cada cuanto se refrescan los datos en segundo plano (pedidos en curso, ventas de hoy)
const REFRESCO_MS = 60_000;

// Dashboard de reportes del negocio para el administrador
@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [
    RouterLink, ReporteFiltrosComponent, ReporteKpisComponent, GraficaVentasComponent, GraficaCategoriasComponent,
    ReporteRankingComponent, ReporteEstadosComponent, MapaCalorComponent
  ],
  templateUrl: './reportes.component.html',
  styleUrl: './reportes.component.scss'
})
export class ReportesComponent implements OnInit {

  @ViewChild('contenido') contenido?: ElementRef<HTMLElement>;

  private reporteService = inject(ReporteService);
  private destroyRef = inject(DestroyRef);

  // Cada rango emitido reinicia la consulta y el refresco automatico
  private rango$ = new Subject<RangoFechas>();

  rango: RangoFechas | null = null;
  reporte: ReporteDashboard | null = null;
  cargando = true;
  error = '';
  ultimaActualizacion: Date | null = null;
  exportandoPdf = false;
  exportandoExcel = false;

  ngOnInit(): void {
    this.rango$.pipe(
      switchMap(rango => timer(0, REFRESCO_MS).pipe(
        // switchMap descarta respuestas viejas si el usuario cambia de periodo a mitad de una consulta
        switchMap(() => this.reporteService.getDashboard(rango).pipe(
          catchError(err => {
            this.error = err.status === 0
              ? 'No se pudo conectar con el servidor.'
              : 'No se pudo cargar el reporte. Intenta de nuevo.';
            this.cargando = false;
            return EMPTY;
          })
        )),
        map(reporte => ({ rango, reporte }))
      )),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(({ reporte }) => {
      this.reporte = reporte;
      this.error = '';
      this.cargando = false;
      this.ultimaActualizacion = new Date();
    });
  }

  // Llega desde el filtro de fechas
  cambiarRango(rango: RangoFechas): void {
    this.rango = rango;
    this.cargando = true;
    this.rango$.next(rango);
  }

  reintentar(): void {
    if (this.rango) {
      this.cambiarRango(this.rango);
    }
  }

  get textoPeriodo(): string {
    if (!this.reporte) {
      return '';
    }
    const { desde, hasta, dias } = this.reporte.periodo;
    return `${fechaLegible(desde)} – ${fechaLegible(hasta)} · ${dias} ${dias === 1 ? 'día' : 'días'}`;
  }

  get horaActualizacion(): string {
    return this.ultimaActualizacion?.toLocaleTimeString('es-CO', { hour: 'numeric', minute: '2-digit' }) ?? '';
  }

  get sinPedidos(): boolean {
    return !!this.reporte && this.reporte.indicadores.pedidos === 0;
  }

  // Descarga el Excel generado por el backend (Apache POI)
  descargarExcel(): void {
    if (!this.rango || this.exportandoExcel) {
      return;
    }
    this.exportandoExcel = true;
    this.reporteService.descargarExcel(this.rango).pipe(
      finalize(() => (this.exportandoExcel = false)),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: archivo => this.guardarArchivo(archivo.contenido, archivo.nombre),
      error: () => (this.error = 'No se pudo generar el Excel.')
    });
  }

  // Toma una "foto" del dashboard y la arma como PDF en el navegador.
  // Las librerias se cargan solo al exportar para no hacer mas pesada la carga inicial.
  async descargarPdf(): Promise<void> {
    const elemento = this.contenido?.nativeElement;
    if (!elemento || !this.rango || this.exportandoPdf) {
      return;
    }
    this.exportandoPdf = true;
    try {
      const [{ default: html2canvas }, { jsPDF }] = await Promise.all([import('html2canvas'), import('jspdf')]);

      // Las medidas se toman en la copia que fotografia html2canvas (sin botones ni filtros).
      // Se miden dentro de onclone porque al terminar html2canvas borra esa copia.
      let medidas = this.medirBloques(elemento);
      const lienzo = await html2canvas(elemento, {
        scale: 2,
        backgroundColor: '#001c2a',
        useCORS: true,
        ignoreElements: nodo => nodo.hasAttribute('data-no-pdf'),
        // html2canvas copia la pagina y las animaciones de entrada vuelven a empezar en la copia;
        // se apagan para que la foto salga con todo visible
        onclone: (documento, raiz) => {
          documento.body.classList.add('exportando-pdf');
          medidas = this.medirBloques(raiz);
        }
      });

      // Hoja A4 horizontal con margen; los saltos de pagina caen entre tarjetas, nunca en medio de una
      const pdf = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });
      const margen = 8;
      const anchoUtil = pdf.internal.pageSize.getWidth() - margen * 2;
      const altoUtil = pdf.internal.pageSize.getHeight() - margen * 2;
      const anchoCss = medidas.ancho;
      const pxPorCss = lienzo.width / anchoCss;
      const altoPaginaCss = (altoUtil * anchoCss) / anchoUtil;
      const cortes = this.cortesDePagina(medidas.bordes, altoPaginaCss, lienzo.height / pxPorCss);

      cortes.forEach(([inicio, fin], i) => {
        const pagina = document.createElement('canvas');
        pagina.width = lienzo.width;
        pagina.height = Math.round((fin - inicio) * pxPorCss);
        pagina.getContext('2d')?.drawImage(lienzo, 0, -Math.round(inicio * pxPorCss));

        if (i > 0) {
          pdf.addPage();
        }
        pdf.setFillColor('#001c2a');
        pdf.rect(0, 0, pdf.internal.pageSize.getWidth(), pdf.internal.pageSize.getHeight(), 'F');
        pdf.addImage(pagina.toDataURL('image/jpeg', 0.92), 'JPEG', margen, margen, anchoUtil, (fin - inicio) * anchoUtil / anchoCss);
      });
      pdf.save(`reporte-corvachos_${this.rango.desde}_a_${this.rango.hasta}.pdf`);
    } catch (e) {
      console.error('Error al generar el PDF', e);
      this.error = 'No se pudo generar el PDF.';
    } finally {
      this.exportandoPdf = false;
    }
  }

  // Ancho del dashboard y borde inferior de cada tarjeta, relativos al inicio del dashboard
  private medirBloques(raiz: HTMLElement): { ancho: number; bordes: number[] } {
    const arriba = raiz.getBoundingClientRect().top;
    const bordes = Array.from(raiz.querySelectorAll('.reportes-cabecera, app-reporte-kpis, .reportes-grid > *, .reportes-vacio'))
      .map(b => b.getBoundingClientRect().bottom - arriba)
      .sort((x, y) => x - y);
    return { ancho: raiz.offsetWidth, bordes };
  }

  // Parte el dashboard en paginas; cada corte cae en el borde de una tarjeta para no partirla
  private cortesDePagina(bordes: number[], altoPagina: number, total: number): [number, number][] {
    const cortes: [number, number][] = [];
    let inicio = 0;
    while (inicio < total - 1) {
      const limite = inicio + altoPagina;
      if (limite >= total) {
        cortes.push([inicio, total]);
        break;
      }
      // Ultimo borde que cabe en la pagina; si una tarjeta sola no cabe, se corta en el limite
      const fin = [...bordes].reverse().find(b => b > inicio + 1 && b <= limite) ?? limite;
      cortes.push([inicio, fin]);
      inicio = fin;
    }
    return cortes;
  }

  private guardarArchivo(contenido: Blob, nombre: string): void {
    const url = URL.createObjectURL(contenido);
    const enlace = document.createElement('a');
    enlace.href = url;
    enlace.download = nombre;
    enlace.click();
    URL.revokeObjectURL(url);
  }
}
