import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Administrador } from '../../../../models/administrador.model';

// Subcomponente con los campos del formulario de administrador
@Component({
  selector: 'app-administrador-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './administrador-campos.component.html',
  styleUrl: './administrador-campos.component.scss'
})
export class AdministradorCamposComponent {
  // Objeto administrador recibido desde el componente padre
  @Input() administrador!: Administrador;
}
