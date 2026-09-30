import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClienteServiceService } from '../../service/cliente-service.service';
import { Cliente } from '../../models/cliente.model';
import { RegistroFormularioComponent } from './components/registro-formulario/registro-formulario.component';

@Component({
  selector: 'app-registro-cliente',
  standalone: true,
  imports: [CommonModule, RegistroFormularioComponent],
  templateUrl: './registro-cliente.component.html',
  styleUrl: './registro-cliente.component.scss'
})
export class RegistroClienteComponent {

  @ViewChild(RegistroFormularioComponent) formulario!: RegistroFormularioComponent;

  constructor(
    private clienteService: ClienteServiceService,
    private router: Router
  ) {}

  onRegistrar(cliente: Cliente): void {
    const existente = this.clienteService.getClientes().find(
      c => c.correo === cliente.correo
    );

    if (existente) {
      this.formulario.setError('Este correo ya esta registrado');
      return;
    }

    this.clienteService.agregarCliente(cliente);
    this.router.navigate(['/clientes/perfil', cliente.clienteId]);
  }

}