import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductoCardComponent } from '../producto-card/producto-card.component';
import { Producto } from '../../../../models/producto.model';

// Componente que representa una sección del menú correspondiente a una categoría
@Component({
  selector: 'app-categoria-seccion',
  standalone: true,
  imports: [CommonModule, ProductoCardComponent],
  templateUrl: './categoria-seccion.component.html',
  styleUrl: './categoria-seccion.component.scss'
})
export class CategoriaSeccionComponent {

  // Nombre de la categoría y lista de productos que pertenecen a ella
  @Input() categoria!: string;
  @Input() productos!: Producto[];

}
