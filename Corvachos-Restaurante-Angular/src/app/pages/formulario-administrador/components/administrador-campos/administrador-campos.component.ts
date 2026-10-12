import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Administrador } from '../../../../models/administrador.model';

@Component({
  selector: 'app-administrador-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './administrador-campos.component.html',
  styleUrl: './administrador-campos.component.scss'
})
export class AdministradorCamposComponent {
  @Input() administrador!: Administrador;
}

