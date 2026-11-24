import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { switchMap } from 'rxjs';
import { ClienteService } from '../../service/cliente.service';
import { AuthService } from '../../service/auth.service';
import { Cliente } from '../../models/cliente.model';
import { RegistroFormularioComponent } from './components/registro-formulario/registro-formulario.component';
import { RuletaBienvenidaComponent } from './components/ruleta-bienvenida/ruleta-bienvenida.component';

// Componente de página para el registro de nuevos clientes
@Component({
  selector: 'app-registro-cliente',
  standalone: true,
  imports: [CommonModule, RegistroFormularioComponent, RuletaBienvenidaComponent],
  templateUrl: './registro-cliente.component.html',
  styleUrl: './registro-cliente.component.scss'
})
export class RegistroClienteComponent {

  // Referencia al formulario hijo para mostrar posibles errores de validación o registro
  @ViewChild(RegistroFormularioComponent) formulario!: RegistroFormularioComponent;

  mostrarRuleta = false;
  correoRegistrado = '';
  private clienteIdCreado: number | null = null;

  constructor(
    private clienteService: ClienteService,
    private authService: AuthService,
    private router: Router
  ) {}

  // Registra al nuevo cliente en el backend, inicia su sesión (pide el token JWT) y muestra la ruleta de bienvenida
  onRegistrar(cliente: Cliente): void {
    this.clienteService.agregarCliente(cliente).pipe(
      switchMap(guardado => this.clienteService.login(guardado.correo, cliente.contrasena ?? ''))
    ).subscribe({
      next: (respuesta) => {
        this.authService.iniciarSesion(respuesta);
        this.clienteIdCreado = respuesta.usuario.clienteId;
        this.correoRegistrado = respuesta.usuario.correo ?? cliente.correo;
        this.mostrarRuleta = true;
      },
      error: (err) => {
        // Muestra el mensaje de error devuelto por el servidor o uno genérico
        const msg = err?.error?.message || 'Este correo ya esta registrado o los datos son invalidos';
        this.formulario.setError(msg);
      }
    });
  }

  // Navega al perfil del cliente una vez que gira o cierra la ruleta de bienvenida
  irAPerfil(): void {
    this.mostrarRuleta = false;
    if (this.clienteIdCreado) {
      this.router.navigate(['/clientes/perfil', this.clienteIdCreado]);
    }
  }

}