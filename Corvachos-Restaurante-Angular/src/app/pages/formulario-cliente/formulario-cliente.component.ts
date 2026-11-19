import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { CarritoService } from '../../service/carrito.service';
import { Cliente } from '../../models/cliente.model';
import { ClienteCamposComponent } from './components/cliente-campos/cliente-campos.component';
import { AlertaService } from '../../service/alerta.service';

// Página de formulario para registrar, editar, desactivar o reactivar un cliente
@Component({
  selector: 'app-formulario-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule, ClienteCamposComponent],
  templateUrl: './formulario-cliente.component.html',
  styleUrl: './formulario-cliente.component.scss'
})
export class FormularioClienteComponent implements OnInit {

  // Objeto cliente enlazado a los campos del formulario
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

  // Variables de estado para controlar la edición, el envío y los errores
  editando: boolean = false;
  guardando: boolean = false;
  error: string = '';

  constructor(
    private clienteService: ClienteService,
    private carritoService: CarritoService,
    private route: ActivatedRoute,
    private router: Router,
    private alertaService: AlertaService
  ) {}

  // Carga los datos del cliente si se recibe un ID en los parámetros de la ruta
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

  // Guarda o actualiza la información del cliente y actualiza su sesión activa
  guardar(): void {
    this.guardando = true;
    this.error = '';

    const peticion = this.editando
      ? this.clienteService.actualizarCliente(this.cliente)
      : this.clienteService.agregarCliente(this.cliente);

    peticion.subscribe({
      next: (guardado) => {
        this.guardando = false;
        this.carritoService.setClienteLogueado(guardado);
        this.alertaService.exito('Los datos del cliente han sido guardados correctamente.');
        this.router.navigate(['/clientes/perfil', guardado.clienteId]);
      },
      error: (err) => {
        console.error('Error al guardar el cliente:', err);
        this.guardando = false;
        this.error = err?.error?.message || 'No fue posible guardar los datos del cliente.';
        this.alertaService.error(this.error);
      }
    });
  }

  // Solicita confirmación y desactiva la cuenta del cliente actual
  async desactivarCuenta(): Promise<void> {
    const confirmado = await this.alertaService.confirmar(
      '¿Estás seguro de que quieres desactivar tu cuenta? Podrás reactivarla más tarde.',
      'Desactivar Cuenta'
    );

    if (confirmado) {
      this.clienteService.desactivarCliente(this.cliente.clienteId).subscribe({
        next: (actualizado) => {
          this.cliente = actualizado;
          this.carritoService.setClienteLogueado(actualizado);
          this.alertaService.exito('La cuenta ha sido desactivada.');
        },
        error: (err) => {
          console.error('Error al desactivar la cuenta:', err);
          this.error = 'No fue posible desactivar la cuenta.';
          this.alertaService.error(this.error);
        }
      });
    }
  }

  // Reactiva la cuenta del cliente y actualiza los datos en la sesión
  reactivarCuenta(): void {
    this.clienteService.reactivarCliente(this.cliente.clienteId).subscribe({
      next: (actualizado) => {
        this.cliente = actualizado;
        this.carritoService.setClienteLogueado(actualizado);
        this.alertaService.exito('La cuenta ha sido reactivada.');
      },
      error: (err) => {
        console.error('Error al reactivar la cuenta:', err);
        this.error = 'No fue posible reactivar la cuenta.';
        this.alertaService.error(this.error);
      }
    });
  }

  // Regresa al perfil del cliente si estaba editando o al listado general si estaba creando
  cancelar(): void {
    if (this.editando) {
      this.router.navigate(['/clientes/perfil', this.cliente.clienteId]);
    } else {
      this.router.navigate(['/clientes']);
    }
  }
}