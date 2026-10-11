import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Adicional } from '../../../../models/adicional.model';
import { Categoria } from '../../../../models/categoria.model';

@Component({
  selector: 'app-adicional-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './adicional-campos.component.html',
  styleUrl: './adicional-campos.component.scss'
})
export class AdicionalCamposComponent {

  @Input() adicional!: Adicional;
  @Input() categorias: Categoria[] = [];
}