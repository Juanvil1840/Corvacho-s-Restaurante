import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { OperadorService } from '../../service/operador.service';
import { OperadorFormularioComponent } from './components/operador-formulario/operador-formulario.component';

// Componente de página para el inicio de sesión de operarios
@Component({
  selector: 'app-login-operario',
  standalone: true,
  imports: [CommonModule, OperadorFormularioComponent],
  templateUrl: './login-operario.component.html',
  styleUrl: './login-operario.component.scss'
})
export class LoginOperarioComponent {

  // Referencia al formulario hijo para mostrar errores de inicio de sesión
  @ViewChild(OperadorFormularioComponent) formulario!: OperadorFormularioComponent;

  constructor(
    private operadorService: OperadorService,
    private router: Router
  ) {}

  // Valida las credenciales del operador, guarda su sesión y navega al portal de operario
  onIniciarSesion(datos: { usuario: string, contrasena: string }): void {
    this.operadorService.login(datos.usuario, datos.contrasena).subscribe({
      next: (operador) => {
        // Solo puede haber una sesion de trabajo a la vez
        localStorage.removeItem('administrador');
        localStorage.setItem('operador', JSON.stringify(operador));
        this.router.navigate(['/portal-operario']);
      },
      error: (err) => {
        // Notifica al formulario si el usuario o la contraseña son incorrectos
        this.formulario.setError('Usuario o contraseña incorrectos');
      }
    });
  }
}
