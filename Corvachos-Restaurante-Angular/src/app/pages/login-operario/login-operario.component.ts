import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { OperadorService } from '../../service/operador.service';
import { OperadorFormularioComponent } from './components/operador-formulario/operador-formulario.component';

@Component({
  selector: 'app-login-operario',
  standalone: true,
  imports: [CommonModule, OperadorFormularioComponent],
  templateUrl: './login-operario.component.html',
  styleUrl: './login-operario.component.scss'
})
export class LoginOperarioComponent {

  @ViewChild(OperadorFormularioComponent) formulario!: OperadorFormularioComponent;

  constructor(
    private operadorService: OperadorService,
    private router: Router
  ) {}

  onIniciarSesion(datos: { usuario: string, contrasena: string }): void {
    this.operadorService.login(datos.usuario, datos.contrasena).subscribe({
      next: (operador) => {
        localStorage.setItem('operador', JSON.stringify(operador));
        this.router.navigate(['/portal-operario']);
      },
      error: (err) => {
        this.formulario.setError('Usuario o contraseña incorrectos');
      }
    });
  }
}