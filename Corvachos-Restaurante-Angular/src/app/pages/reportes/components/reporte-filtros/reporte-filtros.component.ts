import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RangoFechas } from '../../../../models/reporte.model';
import { desdeIso, fechaIso } from '../../reporte-formato';

type Preset = '7' | '30' | '90' | 'mes' | 'personalizado';

// Selector del periodo del reporte: atajos rapidos o un rango personalizado
@Component({
  selector: 'app-reporte-filtros',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './reporte-filtros.component.html',
  styleUrl: './reporte-filtros.component.scss'
})
export class ReporteFiltrosComponent implements OnInit {

  // Emite el rango elegido para que la pagina consulte el backend
  @Output() rangoCambiado = new EventEmitter<RangoFechas>();

  readonly presets: { id: Preset; texto: string }[] = [
    { id: '7', texto: '7 días' },
    { id: '30', texto: '30 días' },
    { id: '90', texto: '90 días' },
    { id: 'mes', texto: 'Este mes' },
    { id: 'personalizado', texto: 'Personalizado' }
  ];

  activo: Preset = '30';
  desde = '';
  hasta = '';
  hoy = fechaIso(new Date());
  error = '';

  ngOnInit(): void {
    this.elegir('30');
  }

  // Calcula el rango del atajo y lo emite (el personalizado espera a "Aplicar")
  elegir(preset: Preset): void {
    this.activo = preset;
    this.error = '';
    if (preset === 'personalizado') {
      return;
    }
    const hoy = new Date();
    const inicio = preset === 'mes'
      ? new Date(hoy.getFullYear(), hoy.getMonth(), 1)
      : new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate() - (Number(preset) - 1));
    this.desde = fechaIso(inicio);
    this.hasta = fechaIso(hoy);
    this.rangoCambiado.emit({ desde: this.desde, hasta: this.hasta });
  }

  // Valida el rango personalizado con las mismas reglas que el backend
  aplicar(): void {
    if (!this.desde || !this.hasta) {
      this.error = 'Selecciona ambas fechas.';
      return;
    }
    const dias = (desdeIso(this.hasta).getTime() - desdeIso(this.desde).getTime()) / 86_400_000 + 1;
    if (dias < 1) {
      this.error = 'La fecha inicial debe ser anterior a la final.';
      return;
    }
    if (dias > 366) {
      this.error = 'El rango máximo es de un año.';
      return;
    }
    this.error = '';
    this.rangoCambiado.emit({ desde: this.desde, hasta: this.hasta });
  }
}
