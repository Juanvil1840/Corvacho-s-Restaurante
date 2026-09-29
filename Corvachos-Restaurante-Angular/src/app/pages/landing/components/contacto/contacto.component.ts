import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-contacto',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contacto.component.html',
  styleUrls: ['./contacto.component.scss']
})
export class ContactoComponent {
  // Referencia al input de teléfono para poder hacer .focus() si hay error
  @ViewChild('telefonoInput') telefonoInput!: ElementRef;

  contacto = {
    nombre: '',
    apellido: '',
    correo: '',
    telefono: '',
    asunto: '',
    mensaje: ''
  };

  telefonoErrorVisible = false;
  telefonoErrorMsg = '';
  respuestaForm = '';

  mostrarErrorTelefono() {
    this.telefonoErrorMsg = 'El problema fue el teléfono: solo se permiten números.';
    this.telefonoErrorVisible = true;
  }

  limpiarErrorTelefono() {
    this.telefonoErrorMsg = '';
    this.telefonoErrorVisible = false;
  }

  onTelefonoInput() {
    const valorOriginal = this.contacto.telefono;
    const valorNumerico = valorOriginal.replace(/[^0-9]/g, '');

    if (valorOriginal !== valorNumerico) {
      this.contacto.telefono = valorNumerico;
      this.mostrarErrorTelefono();
    } else if (valorOriginal) {
      this.limpiarErrorTelefono();
    }
  }

  enviarMensaje() {
    const telefonoVal = this.contacto.telefono;

    // Validación equivalente al evento 'submit' de JS
    if (!telefonoVal || !/^[0-9]+$/.test(telefonoVal)) {
      this.mostrarErrorTelefono();
      this.respuestaForm = 'No se pudo enviar el formulario. El problema fue el teléfono: solo se permiten números.';
      
      if (this.telefonoInput) {
        this.telefonoInput.nativeElement.focus();
      }
      return;
    }

    // Si pasa la validación correctamente
    this.limpiarErrorTelefono();
    console.log('Datos del formulario de contacto:', this.contacto);
    this.respuestaForm = '¡Mensaje enviado con éxito! Nos pondremos en contacto pronto.';
    
    // Limpiar formulario
    this.contacto = { nombre: '', apellido: '', correo: '', telefono: '', asunto: '', mensaje: '' };
  }
}