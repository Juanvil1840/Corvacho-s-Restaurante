import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Componente de formulario para capturar las credenciales de un administrador
@Component({
  selector: 'app-admin-formulario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-formulario.component.html',
  styleUrl: './admin-formulario.component.scss'
})
export class AdminFormularioComponent {

  // Campos del formulario y mensaje de error
  usuario: string = '';
  contrasena: string = '';
  error: string = '';

  // Evento que envía las credenciales ingresadas al componente padre
  @Output() iniciarSesion = new EventEmitter<{ usuario: string, contrasena: string }>();

  // Emite los datos del formulario al enviarse
  onSubmit(): void {
    this.iniciarSesion.emit({
      usuario: this.usuario,
      contrasena: this.contrasena
    });
  }

  // Permite establecer un mensaje de error desde el componente padre
  setError(mensaje: string): void {
    this.error = mensaje;
  }
}
