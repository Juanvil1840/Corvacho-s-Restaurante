import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { Cliente } from '../../models/cliente.model';
import { ClienteTablaComponent } from './components/cliente-tabla/cliente-tabla.component';

// Componente para visualizar y gestionar el listado de clientes registrados
@Component({
  selector: 'app-lista-clientes',
  standalone: true,
  imports: [CommonModule, RouterLink, ClienteTablaComponent],
  templateUrl: './lista-clientes.component.html',
  styleUrl: './lista-clientes.component.scss'
})
export class ListaClientesComponent implements OnInit {

  // Arreglo de clientes y estados para controlar la interfaz
  clientes: Cliente[] = [];
  cargando = false;
  error = '';

  constructor(private clienteService: ClienteService) {}

  // Carga los clientes al iniciar el componente
  ngOnInit(): void {
    this.cargarClientes();
  }

  // Solicita al backend la lista completa de clientes
  cargarClientes(): void {
    this.cargando = true;
    this.error = '';

    this.clienteService.getClientes().subscribe({
      next: (clientes) => {
        this.clientes = clientes;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar los clientes:', err);
        this.error = 'No fue posible cargar los clientes. Intenta nuevamente.';
        this.cargando = false;
      }
    });
  }

  // Pide confirmación y elimina un cliente según su ID
  eliminarCliente(id: number): void {
    if (confirm('¿Estas seguro de eliminar este cliente?')) {
      this.clienteService.eliminarCliente(id).subscribe({
        next: () => {
          // Recarga el listado de clientes tras eliminar
          this.cargarClientes();
        },
        error: (err) => {
          console.error('Error al eliminar cliente:', err);
          this.error = 'No fue posible eliminar el cliente.';
        }
      });
    }
  }

}
