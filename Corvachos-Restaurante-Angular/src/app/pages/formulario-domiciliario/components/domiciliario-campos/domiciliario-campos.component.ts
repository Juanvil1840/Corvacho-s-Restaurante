import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Domiciliario } from '../../../../models/domiciliario.model';
import { Operador } from '../../../../models/operador.model';

@Component({
  selector: 'app-domiciliario-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './domiciliario-campos.component.html',
  styleUrl: './domiciliario-campos.component.scss'
})
export class DomiciliarioCamposComponent {

  @Input() domiciliario!: Domiciliario;
  @Input() operadores: Operador[] = [];
}
