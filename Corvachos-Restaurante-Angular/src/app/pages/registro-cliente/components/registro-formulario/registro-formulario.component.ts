import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Cliente } from '../../../../models/cliente.model';

@Component({
  selector: 'app-registro-formulario',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registro-formulario.component.html',
  styleUrl: './registro-formulario.component.scss'
})
export class RegistroFormularioComponent {

  cliente: Cliente = {
    clienteId: 0,
    nombre: '',
    apellido: '',
    correo: '',
    contrasena: '',
    telefono: '',
    direccion: '',
    activo: true
  };

  error: string = '';

  @Output() registrar = new EventEmitter<Cliente>();

  onSubmit(): void {
    this.registrar.emit(this.cliente);
  }

  setError(mensaje: string): void {
    this.error = mensaje;
  }

}