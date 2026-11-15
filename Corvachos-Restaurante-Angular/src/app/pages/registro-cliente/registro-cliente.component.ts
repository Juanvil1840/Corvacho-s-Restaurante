import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { switchMap } from 'rxjs';
import { ClienteService } from '../../service/cliente.service';
import { AuthService } from '../../service/auth.service';
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
    private authService: AuthService,
    private router: Router
  ) {}

  // Registra al nuevo cliente en el backend, inicia su sesión (pide el token JWT) y lo redirige a su perfil
  onRegistrar(cliente: Cliente): void {
    this.clienteService.agregarCliente(cliente).pipe(
      switchMap(guardado => this.clienteService.login(guardado.correo, cliente.contrasena ?? ''))
    ).subscribe({
      next: (respuesta) => {
        this.authService.iniciarSesion(respuesta);
        this.router.navigate(['/clientes/perfil', respuesta.usuario.clienteId]);
      },
      error: (err) => {
        // Muestra el mensaje de error devuelto por el servidor o uno genérico
        const msg = err?.error?.message || 'Este correo ya esta registrado o los datos son invalidos';
        this.formulario.setError(msg);
      }
    });
  }

}
