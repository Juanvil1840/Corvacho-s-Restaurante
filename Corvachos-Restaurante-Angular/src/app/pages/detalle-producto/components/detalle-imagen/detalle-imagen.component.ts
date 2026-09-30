import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Producto } from '../../../../models/producto.model';

@Component({
  selector: 'app-detalle-imagen',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detalle-imagen.component.html',
  styleUrl: './detalle-imagen.component.scss'
})
export class DetalleImagenComponent {

  @Input() producto!: Producto;

}