import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Cliente } from '../../../../models/cliente.model';

@Component({
  selector: 'app-cliente-fila',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cliente-fila.component.html',
  styleUrl: './cliente-fila.component.scss'
})
export class ClienteFilaComponent {

  @Input() cliente!: Cliente;
  @Output() eliminar = new EventEmitter<number>();

  eliminarCliente(): void {
    this.eliminar.emit(this.cliente.clienteId);
  }

}