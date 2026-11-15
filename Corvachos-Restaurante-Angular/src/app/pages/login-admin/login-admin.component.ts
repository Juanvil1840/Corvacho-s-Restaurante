import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AdministradorService } from '../../service/administrador.service';
import { AuthService } from '../../service/auth.service';
import { AdminFormularioComponent } from './components/admin-formulario/admin-formulario.component';

// Componente de página para el inicio de sesión de administradores
@Component({
  selector: 'app-login-admin',
  standalone: true,
  imports: [CommonModule, AdminFormularioComponent],
  templateUrl: './login-admin.component.html',
  styleUrl: './login-admin.component.scss'
})
export class LoginAdminComponent {

  // Referencia al formulario hijo para mostrar mensajes de error
  @ViewChild(AdminFormularioComponent) formulario!: AdminFormularioComponent;

  constructor(
    private administradorService: AdministradorService,
    private authService: AuthService,
    private router: Router
  ) {}

  // Valida las credenciales del administrador e inicia su sesión
  onIniciarSesion(datos: { usuario: string, contrasena: string }): void {
    this.administradorService.login(datos.usuario, datos.contrasena).subscribe({
      next: (respuesta) => {
        // Guarda el token JWT y los datos del admin (cierra cualquier otra sesión)
        this.authService.iniciarSesion(respuesta);
        this.router.navigate([this.authService.rutaInicio(respuesta.rol)]);
      },
      error: (err) => {
        // Muestra un mensaje de error en el formulario si las credenciales fallan
        this.formulario.setError('Usuario o contraseña incorrectos');
      }
    });
  }
}
