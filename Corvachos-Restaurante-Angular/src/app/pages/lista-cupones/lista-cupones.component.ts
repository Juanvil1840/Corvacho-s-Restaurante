import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CuponService } from '../../service/cupon.service';
import { Cupon } from '../../models/cupon.model';
import { CuponTablaComponent } from './components/cupon-tabla/cupon-tabla.component';

// Página del administrador para ver y gestionar los cupones de descuento
@Component({
  selector: 'app-lista-cupones',
  standalone: true,
  imports: [RouterLink, CuponTablaComponent],
  templateUrl: './lista-cupones.component.html',
  styleUrl: './lista-cupones.component.scss'
})
export class ListaCuponesComponent implements OnInit {

  cupones: Cupon[] = [];
  cargando = false;
  error = '';

  constructor(private cuponService: CuponService) {}

  ngOnInit(): void {
    this.cargarCupones();
  }

  cargarCupones(): void {
    this.cargando = true;
    this.error = '';
    this.cuponService.getCupones().subscribe({
      next: (cupones) => {
        this.cupones = cupones;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar los cupones:', error);
        this.error = 'No fue posible cargar los cupones. Intenta nuevamente.';
        this.cargando = false;
      }
    });
  }

  // Si el cupón ya se usó en pedidos el backend no deja borrarlo y explica que se puede desactivar
  eliminarCupon(id: number): void {
    if (!confirm('¿Eliminar este cupón?')) {
      return;
    }
    this.cuponService.eliminarCupon(id).subscribe({
      next: () => this.cargarCupones(),
      error: (error) => {
        console.error('Error al eliminar el cupón:', error);
        this.error = error.error?.mensaje || 'No fue posible eliminar el cupón.';
      }
    });
  }

  // Activa o desactiva el cupón sin entrar al formulario
  cambiarActivo(cupon: Cupon): void {
    this.cuponService.actualizarCupon({ ...cupon, activo: !cupon.activo }).subscribe({
      next: () => this.cargarCupones(),
      error: (error) => {
        console.error('Error al actualizar el cupón:', error);
        this.error = error.error?.mensaje || 'No fue posible actualizar el cupón.';
      }
    });
  }
}
