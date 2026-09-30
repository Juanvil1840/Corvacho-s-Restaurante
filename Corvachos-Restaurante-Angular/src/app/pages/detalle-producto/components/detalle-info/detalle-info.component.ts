import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Producto } from '../../../../models/producto.model';
import { Adicional } from '../../../../models/adicional.model';

@Component({
  selector: 'app-detalle-info',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './detalle-info.component.html',
  styleUrl: './detalle-info.component.scss'
})
export class DetalleInfoComponent {

  @Input() producto!: Producto;
  @Input() adicionales!: Adicional[];

}