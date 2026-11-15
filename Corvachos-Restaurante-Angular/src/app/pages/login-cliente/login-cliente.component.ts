import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { AuthService } from '../../service/auth.service';
import { LoginFormularioComponent } from './components/login-formulario/login-formulario.component';

// Componente de página para el inicio de sesión de clientes
@Component({
  selector: 'app-login-cliente',
  standalone: true,
  imports: [CommonModule, LoginFormularioComponent],
  templateUrl: './login-cliente.component.html',
  styleUrl: './login-cliente.component.scss'
})
export class LoginClienteComponent {

  // Referencia al formulario hijo para gestionar errores de autenticación
  @ViewChild(LoginFormularioComponent) formulario!: LoginFormularioComponent;

  constructor(
    private clienteService: ClienteService,
    private authService: AuthService,
    private router: Router
  ) {}

  // Autentica al cliente, guarda su sesión en el carrito y redirige a su perfil
  onIniciarSesion(datos: { correo: string, contrasena: string }): void {
    this.clienteService.login(datos.correo, datos.contrasena).subscribe({
      next: (respuesta) => {
        // Guarda el token JWT y el cliente (también carga su carrito)
        this.authService.iniciarSesion(respuesta);
        this.router.navigate(['/clientes/perfil', respuesta.usuario.clienteId]);
      },
      error: (err) => {
        // Verifica si la cuenta está desactivada o si las credenciales son inválidas
        if (err.status === 403) {
          this.formulario.setError('Tu cuenta se encuentra desactivada.');
        } else {
          this.formulario.setError('Usuario o contrasena incorrectos');
        }
      }
    });
  }

}
