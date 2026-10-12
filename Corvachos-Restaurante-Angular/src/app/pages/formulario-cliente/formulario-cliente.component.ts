import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { Cliente } from '../../models/cliente.model';
import { ClienteCamposComponent } from './components/cliente-campos/cliente-campos.component';

@Component({
  selector: 'app-formulario-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule, ClienteCamposComponent],
  templateUrl: './formulario-cliente.component.html',
  styleUrl: './formulario-cliente.component.scss'
})
export class FormularioClienteComponent implements OnInit {

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

  editando: boolean = false;
  guardando: boolean = false;
  error: string = '';

  constructor(
    private clienteService: ClienteService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editando = true;
      this.clienteService.getClienteById(Number(id)).subscribe({
        next: (clienteExistente) => {
          this.cliente = { ...clienteExistente };
        },
        error: (err) => {
          console.error('Error al cargar el cliente:', err);
          this.error = 'No fue posible cargar la información del cliente.';
        }
      });
    }
  }

  guardar(): void {
    this.guardando = true;
    this.error = '';

    const peticion = this.editando
      ? this.clienteService.actualizarCliente(this.cliente)
      : this.clienteService.agregarCliente(this.cliente);

    peticion.subscribe({
      next: (guardado) => {
        this.guardando = false;
        this.router.navigate(['/clientes/perfil', guardado.clienteId]);
      },
      error: (err) => {
        console.error('Error al guardar el cliente:', err);
        this.guardando = false;
        this.error = err?.error?.message || 'No fue posible guardar los datos del cliente.';
      }
    });
  }

  desactivarCuenta(): void {
    if (confirm('¿Estás seguro de que quieres desactivar tu cuenta? Podrás reactivarla más tarde.')) {
      this.clienteService.desactivarCliente(this.cliente.clienteId).subscribe({
        next: (actualizado) => {
          this.cliente = actualizado;
        },
        error: (err) => {
          console.error('Error al desactivar la cuenta:', err);
          this.error = 'No fue posible desactivar la cuenta.';
        }
      });
    }
  }

  reactivarCuenta(): void {
    this.clienteService.reactivarCliente(this.cliente.clienteId).subscribe({
      next: (actualizado) => {
        this.cliente = actualizado;
      },
      error: (err) => {
        console.error('Error al reactivar la cuenta:', err);
        this.error = 'No fue posible reactivar la cuenta.';
      }
    });
  }

  cancelar(): void {
    if (this.editando) {
      this.router.navigate(['/clientes/perfil', this.cliente.clienteId]);
    } else {
      this.router.navigate(['/clientes']);
    }
  }

}