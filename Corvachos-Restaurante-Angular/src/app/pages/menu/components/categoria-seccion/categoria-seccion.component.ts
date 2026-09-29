import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductoCardComponent } from '../producto-card/producto-card.component';
import { Producto } from '../../../../models/producto.model';

@Component({
  selector: 'app-categoria-seccion',
  standalone: true,
  imports: [CommonModule, ProductoCardComponent],
  templateUrl: './categoria-seccion.component.html',
  styleUrl: './categoria-seccion.component.scss'
})
export class CategoriaSeccionComponent {

  @Input() categoria!: string;
  @Input() productos!: Producto[];

}