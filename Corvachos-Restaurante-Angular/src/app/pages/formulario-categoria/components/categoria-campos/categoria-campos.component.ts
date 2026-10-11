import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Categoria } from '../../../../models/categoria.model';

@Component({
  selector: 'app-categoria-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categoria-campos.component.html',
  styleUrl: './categoria-campos.component.scss'
})
export class CategoriaCamposComponent {

  @Input() categoria!: Categoria;
}