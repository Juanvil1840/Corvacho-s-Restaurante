import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-mensaje',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './error-mensaje.component.html',
  styleUrl: './error-mensaje.component.scss'
})
export class ErrorMensajeComponent {

  // Recibe el mensaje desde la pagina principal
  @Input() mensaje: string = 'La página que buscas no existe o ocurrió un problema inesperado.';
}