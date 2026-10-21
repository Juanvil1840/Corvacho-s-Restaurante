import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { CarritoServiceService } from '../../service/carrito-service.service';
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
    private clienteService: ClienteService,
    private carritoService: CarritoServiceService,
    private router: Router
  ) {}

  onIniciarSesion(datos: { correo: string, contrasena: string }): void {
    this.clienteService.login(datos.correo, datos.contrasena).subscribe({
      next: (clienteEncontrado) => {
        this.carritoService.setClienteLogueado(clienteEncontrado);
        this.router.navigate(['/clientes/perfil', clienteEncontrado.clienteId]);
      },
      error: (err) => {
        if (err.status === 403) {
          this.formulario.setError('Tu cuenta se encuentra desactivada.');
        } else {
          this.formulario.setError('Usuario o contrasena incorrectos');
        }
      }
    });
  }

}
