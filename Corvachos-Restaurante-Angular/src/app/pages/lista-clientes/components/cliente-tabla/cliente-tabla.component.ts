import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Cliente } from '../../../../models/cliente.model';

@Component({
  selector: 'app-cliente-tabla',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cliente-tabla.component.html',
  styleUrl: './cliente-tabla.component.scss'
})
export class ClienteTablaComponent {

  @Input() clientes!: Cliente[];
  @Output() eliminar = new EventEmitter<number>();

  eliminarCliente(id: number): void {
    this.eliminar.emit(id);
  }

}