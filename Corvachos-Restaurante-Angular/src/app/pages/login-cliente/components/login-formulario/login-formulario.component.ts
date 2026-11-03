import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

// Componente de formulario para el inicio de sesión del cliente
@Component({
  selector: 'app-login-formulario',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login-formulario.component.html',
  styleUrl: './login-formulario.component.scss'
})
export class LoginFormularioComponent {

  // Campos de entrada para correo, contraseña y mensaje de error
  correo: string = '';
  contrasena: string = '';
  error: string = '';

  // Evento que emite las credenciales al componente padre cuando se envía el formulario
  @Output() iniciarSesion = new EventEmitter<{ correo: string, contrasena: string }>();

  // Envía las credenciales capturadas hacia el componente padre
  onSubmit(): void {
    this.iniciarSesion.emit({
      correo: this.correo,
      contrasena: this.contrasena
    });
  }

  // Actualiza el mensaje de error mostrado en el formulario
  setError(mensaje: string): void {
    this.error = mensaje;
  }

}
