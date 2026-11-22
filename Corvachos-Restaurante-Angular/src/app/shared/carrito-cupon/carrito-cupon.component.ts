import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CuponService } from '../../service/cupon.service';
import { CuponValidado } from '../../models/cupon.model';

// Campo del carrito para escribir un cupón de descuento.
// Muestra el descuento y avisa al carrito; si cambia el valor del carrito se vuelve a validar
@Component({
  selector: 'app-carrito-cupon',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  templateUrl: './carrito-cupon.component.html',
  styleUrl: './carrito-cupon.component.scss'
})
export class CarritoCuponComponent implements OnChanges {

  // Valor del carrito antes del descuento
  @Input({ required: true }) subtotal = 0;
  // Cupón aplicado (o null al quitarlo o si deja de servir)
  @Output() cambio = new EventEmitter<CuponValidado | null>();

  codigo = '';
  aplicado: CuponValidado | null = null;
  validando = false;
  error = '';

  constructor(private cuponService: CuponService) {}

  // Si el cliente agrega o quita comidas, el descuento cambia (o el cupón deja de cumplir la compra mínima)
  ngOnChanges(cambios: SimpleChanges): void {
    if (cambios['subtotal'] && !cambios['subtotal'].firstChange && this.aplicado) {
      this.validar(this.aplicado.codigo);
    }
  }

  aplicar(): void {
    const codigo = this.codigo.trim();
    if (codigo) {
      this.validar(codigo);
    }
  }

  quitar(): void {
    this.aplicado = null;
    this.codigo = '';
    this.error = '';
    this.cambio.emit(null);
  }

  private validar(codigo: string): void {
    this.validando = true;
    this.error = '';
    this.cuponService.validarCupon(codigo, this.subtotal).subscribe({
      next: (cupon) => {
        this.validando = false;
        this.aplicado = cupon;
        this.cambio.emit(cupon);
      },
      error: (err) => {
        this.validando = false;
        this.aplicado = null;
        this.cambio.emit(null);
        this.error = err?.status === 401 || err?.status === 403
          ? 'Inicia sesión para usar un cupón.'
          : err?.error?.mensaje || 'No se pudo validar el cupón. Intenta de nuevo.';
      }
    });
  }
}
