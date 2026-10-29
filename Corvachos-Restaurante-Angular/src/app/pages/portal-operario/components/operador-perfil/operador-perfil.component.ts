import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-operador-perfil',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './operador-perfil.component.html',
  styleUrl: './operador-perfil.component.scss'
})
export class OperadorPerfilComponent {

  @Input() nombreOperador: string = '';
  
  @Output() cerrar = new EventEmitter<void>();

  onCerrarSesion(): void {
    this.cerrar.emit();
  }
}