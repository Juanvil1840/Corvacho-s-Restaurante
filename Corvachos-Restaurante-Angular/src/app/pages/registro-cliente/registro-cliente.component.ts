import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ClienteServiceService } from '../../service/cliente-service.service';
import { Cliente } from '../../models/cliente.model';
import { FooterComponent } from '../../shared/footer/footer.component';

@Component({
  selector: 'app-registro-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registro-cliente.component.html',
  styleUrl: './registro-cliente.component.scss'
})
export class RegistroClienteComponent {

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

  constructor(
    private clienteService: ClienteServiceService,
    private router: Router
  ) {}

  registrar(): void {
    // Verificar que el correo no exista
    const existente = this.clienteService.getClientes().find(
      c => c.correo === this.cliente.correo
    );

    if (existente) {
      this.error = 'Este correo ya esta registrado';
      return;
    }

    // Registrar el nuevo cliente
    this.clienteService.agregarCliente(this.cliente);
    this.router.navigate(['/clientes/perfil', this.cliente.clienteId]);
  }
}