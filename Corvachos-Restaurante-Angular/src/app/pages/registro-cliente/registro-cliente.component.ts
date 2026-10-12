import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
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
    private clienteService: ClienteService,
    private router: Router
  ) {}

  onRegistrar(cliente: Cliente): void {
    this.clienteService.agregarCliente(cliente).subscribe({
      next: (guardado) => {
        this.router.navigate(['/clientes/perfil', guardado.clienteId]);
      },
      error: (err) => {
        const msg = err?.error?.message || 'Este correo ya esta registrado o los datos son invalidos';
        this.formulario.setError(msg);
      }
    });
  }

}