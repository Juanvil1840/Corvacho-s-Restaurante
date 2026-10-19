import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-operador-formulario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './operador-formulario.component.html',
  styleUrl: './operador-formulario.component.scss'
})
export class OperadorFormularioComponent {

  usuario: string = '';
  contrasena: string = '';
  error: string = '';

  @Output() iniciarSesion = new EventEmitter<{ usuario: string, contrasena: string }>();

  onSubmit(): void {
    this.iniciarSesion.emit({
      usuario: this.usuario,
      contrasena: this.contrasena
    });
  }

  setError(mensaje: string): void {
    this.error = mensaje;
  }
}