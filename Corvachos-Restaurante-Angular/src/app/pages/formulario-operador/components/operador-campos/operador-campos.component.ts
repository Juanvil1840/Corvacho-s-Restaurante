import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Operador } from '../../../../models/operador.model';

@Component({
  selector: 'app-operador-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './operador-campos.component.html',
  styleUrl: './operador-campos.component.scss'
})
export class OperadorCamposComponent {
  @Input() operador!: Operador;
}

