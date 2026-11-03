import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { CarritoService } from '../../service/carrito.service';
import { Cliente } from '../../models/cliente.model';
import { RegistroFormularioComponent } from './components/registro-formulario/registro-formulario.component';

// Componente de página para el registro de nuevos clientes
@Component({
  selector: 'app-registro-cliente',
  standalone: true,
  imports: [CommonModule, RegistroFormularioComponent],
  templateUrl: './registro-cliente.component.html',
  styleUrl: './registro-cliente.component.scss'
})
export class RegistroClienteComponent {

  // Referencia al formulario hijo para mostrar posibles errores de validación o registro
  @ViewChild(RegistroFormularioComponent) formulario!: RegistroFormularioComponent;

  constructor(
    private clienteService: ClienteService,
    private carritoService: CarritoService,
    private router: Router
  ) {}

  // Registra al nuevo cliente en el backend, inicia su sesión y lo redirige a su perfil
  onRegistrar(cliente: Cliente): void {
    this.clienteService.agregarCliente(cliente).subscribe({
      next: (guardado) => {
        this.carritoService.setClienteLogueado(guardado);
        this.router.navigate(['/clientes/perfil', guardado.clienteId]);
      },
      error: (err) => {
        // Muestra el mensaje de error devuelto por el servidor o uno genérico
        const msg = err?.error?.message || 'Este correo ya esta registrado o los datos son invalidos';
        this.formulario.setError(msg);
      }
    });
  }

}
