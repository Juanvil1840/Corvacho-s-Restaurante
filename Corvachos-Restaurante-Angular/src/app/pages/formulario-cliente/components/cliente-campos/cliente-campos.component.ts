import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Cliente } from '../../../../models/cliente.model';

// Subcomponente que agrupa los campos de texto para los datos personales del cliente
@Component({
  selector: 'app-cliente-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cliente-campos.component.html',
  styleUrl: './cliente-campos.component.scss'
})
export class ClienteCamposComponent {

  // Cliente cuyos datos se enlazan en los inputs del formulario
  @Input() cliente!: Cliente;

}
