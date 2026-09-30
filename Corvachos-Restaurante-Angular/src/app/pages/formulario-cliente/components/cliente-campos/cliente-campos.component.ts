import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Cliente } from '../../../../models/cliente.model';

@Component({
  selector: 'app-cliente-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cliente-campos.component.html',
  styleUrl: './cliente-campos.component.scss'
})
export class ClienteCamposComponent {

  @Input() cliente!: Cliente;

}