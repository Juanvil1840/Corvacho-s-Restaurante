import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Pedido } from '../../../../models/pedido.model';
import { ESTADOS_PEDIDO } from '../../../../models/estado-pedido.model';

@Component({
  selector: 'app-pedido-cabecera',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedido-cabecera.component.html',
  styleUrl: './pedido-cabecera.component.scss'
})
export class PedidoCabeceraComponent {
  
  @Input() pedido!: Pedido;

  // Calcula el numero del estado actual (1 a 4) para pintar la barra de progreso
  private numeroEstadoActual(): number {
    const posicion = ESTADOS_PEDIDO.indexOf(this.pedido?.estado || '');
    return posicion >= 0 ? posicion + 1 : 1;
  }

  // Devuelve la clase CSS (activo, completado o vacio) segun el paso
  pasoEstado(paso: number): 'activo' | 'completado' | '' {
    const actual = this.numeroEstadoActual();
    if (paso === actual) return 'activo';
    if (paso < actual) return 'completado';
    return '';
  }

  // Revisa si la linea entre pasos ya debe estar pintada de color
  lineaCompletada(despuesDePaso: number): boolean {
    return this.numeroEstadoActual() > despuesDePaso;
  }
}