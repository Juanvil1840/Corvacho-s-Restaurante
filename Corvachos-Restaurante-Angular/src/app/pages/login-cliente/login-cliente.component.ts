import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClienteServiceService } from '../../service/cliente-service.service';
import { LoginFormularioComponent } from './components/login-formulario/login-formulario.component';

@Component({
  selector: 'app-login-cliente',
  standalone: true,
  imports: [CommonModule, LoginFormularioComponent],
  templateUrl: './login-cliente.component.html',
  styleUrl: './login-cliente.component.scss'
})
export class LoginClienteComponent {

  @ViewChild(LoginFormularioComponent) formulario!: LoginFormularioComponent;

  constructor(
    private clienteService: ClienteServiceService,
    private router: Router
  ) {}

  onIniciarSesion(datos: { correo: string, contrasena: string }): void {
    const clienteEncontrado = this.clienteService.getClientes().find(
      c => c.correo === datos.correo && c.contrasena === datos.contrasena
    );

    if (clienteEncontrado) {
      this.router.navigate(['/clientes/perfil', clienteEncontrado.clienteId]);
    } else {
      this.formulario.setError('Usuario o contrasena incorrectos');
    }
  }

}