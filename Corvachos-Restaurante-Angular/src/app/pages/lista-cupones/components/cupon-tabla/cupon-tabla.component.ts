import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Cupon } from '../../../../models/cupon.model';

// Tabla con los cupones: descuento, condiciones, usos y estado
@Component({
  selector: 'app-cupon-tabla',
  standalone: true,
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './cupon-tabla.component.html',
  styleUrl: './cupon-tabla.component.scss'
})
export class CuponTablaComponent {

  @Input() cupones: Cupon[] = [];
  @Output() eliminar = new EventEmitter<number>();
  @Output() cambiarActivo = new EventEmitter<Cupon>();

  // Porcentaje de usos consumidos (para la barrita); 0 si no tiene límite
  porcentajeUsos(cupon: Cupon): number {
    return cupon.usosMaximos ? Math.min(100, (cupon.usos * 100) / cupon.usosMaximos) : 0;
  }
}
