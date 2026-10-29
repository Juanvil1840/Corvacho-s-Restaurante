import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-perfil',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-perfil.component.html',
  styleUrl: './admin-perfil.component.scss'
})
export class AdminPerfilComponent {

  @Input() nombreAdmin: string = '';
  @Input() idAdmin: number = 0;

  @Output() cerrar = new EventEmitter<void>();

  onCerrarSesion(): void {
    this.cerrar.emit();
  }
}