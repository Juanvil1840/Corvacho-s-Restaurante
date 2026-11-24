import { Directive, ElementRef, Input, OnChanges, OnDestroy } from '@angular/core';
import { TipoFormato, formatear } from './reporte-formato';

// Anima un numero desde su valor anterior hasta el nuevo (efecto "contador") y lo formatea
@Directive({
  selector: '[appContador]',
  standalone: true
})
export class ContadorDirective implements OnChanges, OnDestroy {

  @Input({ required: true }) appContador = 0;
  @Input() formato: TipoFormato = 'entero';
  @Input() duracion = 900;

  private actual = 0;
  private frame = 0;

  constructor(private elemento: ElementRef<HTMLElement>) { }

  ngOnChanges(): void {
    cancelAnimationFrame(this.frame);
    const inicio = this.actual;
    const fin = this.appContador ?? 0;

    // Respeta a quien pidio menos movimiento en su sistema operativo
    if (window.matchMedia?.('(prefers-reduced-motion: reduce)').matches) {
      this.pintar(fin);
      return;
    }

    const t0 = performance.now();
    const paso = (ahora: number) => {
      const progreso = Math.min((ahora - t0) / this.duracion, 1);
      const suavizado = 1 - Math.pow(1 - progreso, 3);
      this.pintar(inicio + (fin - inicio) * suavizado);
      if (progreso < 1) {
        this.frame = requestAnimationFrame(paso);
      }
    };
    this.frame = requestAnimationFrame(paso);
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.frame);
  }

  private pintar(valor: number): void {
    this.actual = valor;
    this.elemento.nativeElement.textContent = formatear(valor, this.formato);
  }
}
