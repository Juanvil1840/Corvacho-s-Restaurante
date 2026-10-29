import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Sección de formulario de contacto con validación numérica para el teléfono
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

  // Datos ingresados por el usuario en el formulario de contacto
  contacto = {
    nombre: '',
    apellido: '',
    correo: '',
    telefono: '',
    asunto: '',
    mensaje: ''
  };

  // Variables de estado para mostrar errores de teléfono y respuesta de envío
  telefonoErrorVisible = false;
  telefonoErrorMsg = '';
  respuestaForm = '';

  // Activa el mensaje de error indicando que el teléfono solo acepta números
  mostrarErrorTelefono() {
    this.telefonoErrorMsg = 'El problema fue el teléfono: solo se permiten números.';
    this.telefonoErrorVisible = true;
  }

  // Oculta y limpia el mensaje de error del campo teléfono
  limpiarErrorTelefono() {
    this.telefonoErrorMsg = '';
    this.telefonoErrorVisible = false;
  }

  // Filtra caracteres no numéricos en tiempo real cuando el usuario escribe el teléfono
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

  // Valida el teléfono y procesa el envío del formulario de contacto
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
