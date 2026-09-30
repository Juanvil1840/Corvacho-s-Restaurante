import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login-formulario',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login-formulario.component.html',
  styleUrl: './login-formulario.component.scss'
})
export class LoginFormularioComponent {

  correo: string = '';
  contrasena: string = '';
  error: string = '';

  @Output() iniciarSesion = new EventEmitter<{ correo: string, contrasena: string }>();

  onSubmit(): void {
    this.iniciarSesion.emit({
      correo: this.correo,
      contrasena: this.contrasena
    });
  }

  setError(mensaje: string): void {
    this.error = mensaje;
  }

}