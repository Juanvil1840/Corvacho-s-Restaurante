import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ClienteServiceService } from '../../service/cliente-service.service';
import { Cliente } from '../../models/cliente.model';
import { ClienteTablaComponent } from './components/cliente-tabla/cliente-tabla.component';

@Component({
  selector: 'app-lista-clientes',
  standalone: true,
  imports: [CommonModule, RouterLink, ClienteTablaComponent],
  templateUrl: './lista-clientes.component.html',
  styleUrl: './lista-clientes.component.scss'
})
export class ListaClientesComponent implements OnInit {

  clientes: Cliente[] = [];

  constructor(private clienteService: ClienteServiceService) {}

  ngOnInit(): void {
    this.clientes = this.clienteService.getClientes();
  }

  eliminarCliente(id: number): void {
    if (confirm('¿Estas seguro de eliminar este cliente?')) {
      this.clienteService.eliminarCliente(id);
      this.clientes = this.clienteService.getClientes();
    }
  }

}