import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Producto } from '../../../../models/producto.model';

// Subcomponente que muestra la imagen del producto en la vista de detalle
@Component({
  selector: 'app-detalle-imagen',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detalle-imagen.component.html',
  styleUrl: './detalle-imagen.component.scss'
})
export class DetalleImagenComponent {

  // Producto cuya imagen se va a mostrar
  @Input() producto!: Producto;

}
