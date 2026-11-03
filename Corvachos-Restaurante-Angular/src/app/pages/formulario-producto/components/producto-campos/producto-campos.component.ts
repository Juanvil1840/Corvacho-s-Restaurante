import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Producto } from '../../../../models/producto.model';
import { Categoria } from '../../../../models/categoria.model';

// Subcomponente presentacional con los campos de entrada del formulario de producto
@Component({
  selector: 'app-producto-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './producto-campos.component.html',
  styleUrl: './producto-campos.component.scss'
})
export class ProductoCamposComponent {

  // Producto que se está creando o editando y lista de categorías disponibles
  @Input() producto!: Producto;
  @Input() categorias!: Categoria[];

}
