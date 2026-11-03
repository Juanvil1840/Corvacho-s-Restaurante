import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Cliente } from '../../../../models/cliente.model';

// Componente de formulario para capturar los datos de registro de un nuevo cliente
@Component({
  selector: 'app-registro-formulario',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registro-formulario.component.html',
  styleUrl: './registro-formulario.component.scss'
})
export class RegistroFormularioComponent {

  // Objeto modelo con los campos iniciales para el registro del cliente
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

  // Mensaje de error para mostrar en caso de fallo en el registro
  error: string = '';

  // Evento que emite los datos del cliente hacia el componente padre
  @Output() registrar = new EventEmitter<Cliente>();

  // Emite el objeto cliente cuando se envía el formulario
  onSubmit(): void {
    this.registrar.emit(this.cliente);
  }

  // Asigna un mensaje de error recibido desde el componente padre
  setError(mensaje: string): void {
    this.error = mensaje;
  }

}
