import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Categoria } from '../../../../models/categoria.model';

// Subcomponente que contiene los campos de entrada para el nombre de la categoría
@Component({
  selector: 'app-categoria-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categoria-campos.component.html',
  styleUrl: './categoria-campos.component.scss'
})
export class CategoriaCamposComponent {

  // Categoría que se edita en el formulario
  @Input() categoria!: Categoria;
}
