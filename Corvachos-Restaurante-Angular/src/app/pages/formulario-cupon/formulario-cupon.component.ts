import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CuponService } from '../../service/cupon.service';
import { Cupon } from '../../models/cupon.model';
import { CuponCamposComponent } from './components/cupon-campos/cupon-campos.component';

// Formulario del administrador para crear o editar un cupón de descuento
@Component({
  selector: 'app-formulario-cupon',
  standalone: true,
  imports: [FormsModule, CuponCamposComponent],
  templateUrl: './formulario-cupon.component.html',
  styleUrl: './formulario-cupon.component.scss'
})
export class FormularioCuponComponent implements OnInit {

  cupon: Cupon = {
    id_cupon: 0,
    codigo: '',
    descripcion: '',
    tipo: 'PORCENTAJE',
    valor: 10,
    compraMinima: 0,
    usosMaximos: null,
    usos: 0,
    fechaVencimiento: null,
    activo: true
  };

  editando = false;
  guardando = false;
  error = '';

  constructor(
    private cuponService: CuponService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      return;
    }
    this.editando = true;
    this.cuponService.getCuponById(Number(id)).subscribe({
      next: (cupon) => (this.cupon = { ...cupon }),
      error: (error) => {
        console.error('Error al cargar el cupón:', error);
        this.error = 'No fue posible cargar el cupón.';
      }
    });
  }

  // El backend valida todo (código repetido, porcentaje mayor a 100, etc.) y devuelve el motivo
  guardar(): void {
    this.guardando = true;
    this.error = '';
    const cupon: Cupon = {
      ...this.cupon,
      codigo: (this.cupon.codigo || '').trim().toUpperCase(),
      usosMaximos: this.cupon.usosMaximos || null,
      fechaVencimiento: this.cupon.fechaVencimiento || null
    };
    const peticion = this.editando
      ? this.cuponService.actualizarCupon(cupon)
      : this.cuponService.agregarCupon(cupon);

    peticion.subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/cupones']);
      },
      error: (error) => {
        console.error('Error al guardar el cupón:', error);
        this.guardando = false;
        this.error = error.error?.mensaje || 'No fue posible guardar el cupón. Intenta nuevamente.';
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/cupones']);
  }
}
