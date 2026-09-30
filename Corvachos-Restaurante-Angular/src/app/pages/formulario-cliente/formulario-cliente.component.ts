import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClienteServiceService } from '../../service/cliente-service.service';
import { Cliente } from '../../models/cliente.model';
import { ClienteCamposComponent } from './components/cliente-campos/cliente-campos.component';

@Component({
  selector: 'app-formulario-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ClienteCamposComponent],
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

  constructor(
    private clienteService: ClienteServiceService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editando = true;
      const clienteExistente = this.clienteService.getClienteById(Number(id));
      if (clienteExistente) {
        this.cliente = { ...clienteExistente };
      }
    }
  }

  guardar(): void {
    if (this.editando) {
      this.clienteService.actualizarCliente(this.cliente);
      this.router.navigate(['/clientes/perfil', this.cliente.clienteId]);
    } else {
      this.clienteService.agregarCliente(this.cliente);
      this.router.navigate(['/clientes/perfil', this.cliente.clienteId]);
    }
  }

  cancelar(): void {
    if (this.editando) {
      this.router.navigate(['/clientes/perfil', this.cliente.clienteId]);
    } else {
      this.router.navigate(['/clientes']);
    }
  }

}