import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClienteService } from '../../service/cliente.service';
import { CarritoService } from '../../service/carrito.service';
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
  cargando = true;

  constructor(
    private clienteService: ClienteService,
    private carritoService: CarritoService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const enSesion = this.carritoService.getClienteLogueado();
    if (enSesion && enSesion.clienteId > 0) {
      this.cliente = enSesion;
    }

    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      const idNum = idParam ? Number(idParam) : (enSesion?.clienteId || 0);

      if (idNum > 0 && !isNaN(idNum)) {
        this.cargando = true;
        this.clienteService.getClienteById(idNum).subscribe({
          next: (cliente) => {
            this.cliente = cliente;
            this.carritoService.setClienteLogueado(cliente);
            this.cargando = false;
          },
          error: (err) => {
            console.error('Error al cargar el perfil del cliente:', err);
            if (enSesion && enSesion.clienteId > 0) {
              this.cliente = enSesion;
            }
            this.cargando = false;
          }
        });
      } else {
        this.cargando = false;
      }
    });
  }

  eliminarCuenta(): void {
    if (confirm('¿Estas seguro de que deseas eliminar tu cuenta? Esta accion no se puede deshacer.')) {
      if (this.cliente) {
        this.clienteService.eliminarCliente(this.cliente.clienteId).subscribe({
          next: () => {
            this.carritoService.cerrarSesionCliente();
            this.router.navigate(['/']);
          },
          error: (err) => {
            console.error('Error al eliminar la cuenta:', err);
          }
        });
      }
    }
  }

  cerrarSesion(): void {
    this.carritoService.cerrarSesionCliente();
    this.router.navigate(['/']);
  }
}
