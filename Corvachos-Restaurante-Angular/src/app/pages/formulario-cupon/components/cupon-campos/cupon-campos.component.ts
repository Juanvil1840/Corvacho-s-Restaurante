import { Component, Input } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import { Cupon } from '../../../../models/cupon.model';

// Campos del formulario de cupón, con una vista previa de cómo lo verá el cliente
@Component({
  selector: 'app-cupon-campos',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  // Usa el formulario del componente padre para que (ngSubmit) incluya estos campos
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
  templateUrl: './cupon-campos.component.html',
  styleUrl: './cupon-campos.component.scss'
})
export class CuponCamposComponent {

  @Input({ required: true }) cupon!: Cupon;

  // Ejemplo de ahorro con una compra de $50.000 (o la compra mínima si es mayor)
  get compraEjemplo(): number {
    return Math.max(50000, this.cupon.compraMinima || 0);
  }

  get ahorroEjemplo(): number {
    const valor = Number(this.cupon.valor) || 0;
    const ahorro = this.cupon.tipo === 'PORCENTAJE' ? (this.compraEjemplo * valor) / 100 : valor;
    return Math.min(Math.round(ahorro), this.compraEjemplo);
  }
}
