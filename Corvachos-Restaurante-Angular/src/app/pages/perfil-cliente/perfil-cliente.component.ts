import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { Cliente } from '../../models/cliente.model';
import { PerfilInfoComponent } from './components/perfil-info/perfil-info.component';
import { PerfilPedidosComponent } from './components/perfil-pedidos/perfil-pedidos.component';

@Component({
  selector: 'app-perfil-cliente',
  standalone: true,
  imports: [CommonModule, RouterLink, PerfilInfoComponent, PerfilPedidosComponent],
  templateUrl: './perfil-cliente.component.html',
  styleUrl: './perfil-cliente.component.scss'
})
export class PerfilClienteComponent implements OnInit {

  cliente: Cliente | undefined;

  constructor(
    private clienteService: ClienteService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.clienteService.getClienteById(Number(id)).subscribe({
        next: (cliente) => {
          this.cliente = cliente;
        },
        error: (err) => {
          console.error('Error al cargar el perfil del cliente:', err);
          this.cliente = undefined;
        }
      });
    }
  }

  eliminarCuenta(): void {
    if (confirm('¿Estas seguro de que deseas eliminar tu cuenta? Esta accion no se puede deshacer.')) {
      if (this.cliente) {
        this.clienteService.eliminarCliente(this.cliente.clienteId).subscribe({
          next: () => {
            this.router.navigate(['/']);
          },
          error: (err) => {
            console.error('Error al eliminar la cuenta:', err);
          }
        });
      }
    }
  }
}