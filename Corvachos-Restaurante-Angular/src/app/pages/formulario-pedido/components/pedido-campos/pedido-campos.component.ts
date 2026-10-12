import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Pedido } from '../../../../models/pedido.model';
import { Cliente } from '../../../../models/cliente.model';
import { Operador } from '../../../../models/operador.model';
import { Domiciliario } from '../../../../models/domiciliario.model';

@Component({
  selector: 'app-pedido-campos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pedido-campos.component.html',
  styleUrl: './pedido-campos.component.scss'
})
export class PedidoCamposComponent {

  @Input() pedido!: Pedido;
  @Input() clientes: Cliente[] = [];
  @Input() operadores: Operador[] = [];
  @Input() domiciliarios: Domiciliario[] = [];

  readonly estados: string[] = ['Recibido', 'Cocinando', 'Enviado', 'Entregado', 'Cancelado'];
}
