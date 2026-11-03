import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Cliente } from '../../../../models/cliente.model';

// Componente que representa una fila individual de un cliente
@Component({
  selector: 'app-cliente-fila',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cliente-fila.component.html',
  styleUrl: './cliente-fila.component.scss'
})
export class ClienteFilaComponent {

  // Datos del cliente a mostrar en la fila
  @Input() cliente!: Cliente;
  // Evento que emite el ID del cliente cuando se solicita eliminarlo
  @Output() eliminar = new EventEmitter<number>();

  // Emite el evento de eliminación con el ID del cliente actual
  eliminarCliente(): void {
    this.eliminar.emit(this.cliente.clienteId);
  }

}
