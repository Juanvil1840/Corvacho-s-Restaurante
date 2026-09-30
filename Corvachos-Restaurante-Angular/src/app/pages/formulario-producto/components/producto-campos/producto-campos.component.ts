import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Producto } from '../../../../models/producto.model';
import { Categoria } from '../../../../models/categoria.model';

@Component({
  selector: 'app-producto-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './producto-campos.component.html',
  styleUrl: './producto-campos.component.scss'
})
export class ProductoCamposComponent {

  @Input() producto!: Producto;
  @Input() categorias!: Categoria[];

}