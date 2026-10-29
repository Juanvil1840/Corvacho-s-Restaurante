import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Componente de formulario para capturar las credenciales de un operador
@Component({
  selector: 'app-operador-formulario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './operador-formulario.component.html',
  styleUrl: './operador-formulario.component.scss'
})
export class OperadorFormularioComponent {

  // Propiedades enlazadas a los campos del formulario y estado de error
  usuario: string = '';
  contrasena: string = '';
  error: string = '';

  // Evento que emite el usuario y contraseña al componente padre
  @Output() iniciarSesion = new EventEmitter<{ usuario: string, contrasena: string }>();

  // Emite las credenciales ingresadas al enviar el formulario
  onSubmit(): void {
    this.iniciarSesion.emit({
      usuario: this.usuario,
      contrasena: this.contrasena
    });
  }

  // Asigna un mensaje de error para mostrarlo en la vista
  setError(mensaje: string): void {
    this.error = mensaje;
  }
}
