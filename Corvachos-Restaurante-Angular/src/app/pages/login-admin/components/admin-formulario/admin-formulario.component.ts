import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-formulario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-formulario.component.html',
  styleUrl: './admin-formulario.component.scss'
})
export class AdminFormularioComponent {

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