import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Cliente } from '../../../../models/cliente.model';

// Componente que muestra la tabla con todos los clientes
@Component({
  selector: 'app-cliente-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cliente-tabla.component.html',
  styleUrl: './cliente-tabla.component.scss'
})
export class ClienteTablaComponent {

  // Lista de clientes recibida desde el componente padre
  @Input() clientes!: Cliente[];
  // Evento para notificar la eliminación de un cliente al componente padre
  @Output() eliminar = new EventEmitter<number>();

  // Emite el ID del cliente que se desea eliminar
  eliminarCliente(id: number): void {
    this.eliminar.emit(id);
  }

}
