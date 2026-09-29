import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ClienteServiceService } from '../../service/cliente-service.service';
import { FooterComponent } from '../../shared/footer/footer.component';

@Component({
  selector: 'app-login-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login-cliente.component.html',
  styleUrl: './login-cliente.component.scss'
})
export class LoginClienteComponent {

  correo: string = '';
  contrasena: string = '';
  error: string = '';

  constructor(
    private clienteService: ClienteServiceService,
    private router: Router
  ) {}

  iniciarSesion(): void {
    const clienteEncontrado = this.clienteService.getClientes().find(
      c => c.correo === this.correo && c.contrasena === this.contrasena
    );

    if (clienteEncontrado) {
      this.router.navigate(['/clientes/perfil', clienteEncontrado.clienteId]);
    } else {
      this.error = 'Usuario o contrasena incorrectos';
    }
  }
}