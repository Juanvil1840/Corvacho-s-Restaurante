import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AdministradorService } from '../../service/administrador.service';
import { AdminFormularioComponent } from './components/admin-formulario/admin-formulario.component';

@Component({
  selector: 'app-login-admin',
  standalone: true,
  imports: [CommonModule, AdminFormularioComponent],
  templateUrl: './login-admin.component.html',
  styleUrl: './login-admin.component.scss'
})
export class LoginAdminComponent {

  @ViewChild(AdminFormularioComponent) formulario!: AdminFormularioComponent;

  constructor(
    private administradorService: AdministradorService,
    private router: Router
  ) {}

  onIniciarSesion(datos: { usuario: string, contrasena: string }): void {
    this.administradorService.login(datos.usuario, datos.contrasena).subscribe({
      next: (admin) => {
        localStorage.setItem('administrador', JSON.stringify(admin));
        this.router.navigate(['/portal-admin']);
      },
      error: (err) => {
        this.formulario.setError('Usuario o contraseña incorrectos');
      }
    });
  }
}