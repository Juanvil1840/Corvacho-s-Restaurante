import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Cliente } from '../../../../models/cliente.model';

@Component({
  selector: 'app-perfil-info',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './perfil-info.component.html',
  styleUrl: './perfil-info.component.scss'
})
export class PerfilInfoComponent {

  @Input() cliente!: Cliente;

  @Output() eliminar = new EventEmitter<void>();

  eliminarCuenta(): void {
    this.eliminar.emit();
  }

}