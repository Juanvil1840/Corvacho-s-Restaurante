import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { CarritoServiceService, ItemCarrito } from '../../service/carrito-service.service';
import { PedidoService } from '../../service/pedido.service';
import { Cliente } from '../../models/cliente.model';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit, OnDestroy {

  esPaginaLogin = false;
  esPortalOperario = false;
  clienteLogueado: Cliente | null = null;
  itemsCarrito: ItemCarrito[] = [];
  carritoAbierto = false;

  enviandoPedido = false;
  errorPedido = '';
  exitoPedido = '';

  private subs: Subscription[] = [];

  constructor(
    private router: Router,
    private carritoService: CarritoServiceService,
    private pedidoService: PedidoService
  ) {}

  ngOnInit(): void {
    this.actualizarEstadoRuta(this.router.url);

    this.subs.push(
      this.router.events.pipe(
        filter(event => event instanceof NavigationEnd)
      ).subscribe((event: any) => {
        const url = event.urlAfterRedirects || event.url;
        this.actualizarEstadoRuta(url);
      })
    );

    this.subs.push(
      this.carritoService.cliente$.subscribe(cli => {
        this.clienteLogueado = cli;
      })
    );

    this.subs.push(
      this.carritoService.items$.subscribe(items => {
        this.itemsCarrito = items;
      })
    );

    this.subs.push(
      this.carritoService.abierto$.subscribe(abierto => {
        this.carritoAbierto = abierto;
        if (!abierto) {
          this.errorPedido = '';
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
  }

  private actualizarEstadoRuta(url: string): void {
    this.esPaginaLogin = url === '/login' || url.startsWith('/login?');
    this.esPortalOperario = url.startsWith('/portal-operario') || url.startsWith('/login-operario');
    this.carritoService.getClienteLogueado();
  }

  get totalCantidad(): number {
    return this.carritoService.getTotalCantidad();
  }

  get totalPrecio(): number {
    return this.carritoService.getTotalPrecio();
  }

  calcularUnitario(item: ItemCarrito): number {
    return this.carritoService.calcularPrecioUnitarioItem(item);
  }

  toggleCarrito(): void {
    this.exitoPedido = '';
    this.errorPedido = '';
    this.carritoService.toggleCarrito();
  }

  cerrarCarrito(): void {
    this.carritoService.cerrarCarrito();
  }

  irAlMenu(): void {
    this.cerrarCarrito();
    this.router.navigate(['/menu']);
  }

  irAlPerfil(): void {
    this.cerrarCarrito();
    const cli = this.carritoService.getClienteLogueado();
    if (cli && cli.clienteId > 0) {
      this.router.navigate(['/clientes/perfil', cli.clienteId]);
    } else {
      this.router.navigate(['/login']);
    }
  }

  modificarCantidad(index: number, delta: number): void {
    this.carritoService.modificarCantidad(index, delta);
  }

  eliminarItem(index: number): void {
    this.carritoService.eliminarItem(index);
  }

  vaciarCarrito(): void {
    this.carritoService.vaciarCarrito();
  }

  confirmarPedido(): void {
    this.errorPedido = '';
    this.exitoPedido = '';

    if (this.itemsCarrito.length === 0) {
      this.errorPedido = 'Tu carrito está vacío. Añade comidas desde el menú.';
      return;
    }

    const cliente = this.carritoService.getClienteLogueado();
    if (!cliente || !cliente.clienteId) {
      this.cerrarCarrito();
      this.router.navigate(['/login']);
      return;
    }

    const nuevoPedido = {
      cliente: { clienteId: cliente.clienteId } as Cliente,
      detallePedidos: this.itemsCarrito.map(item => ({
        cantidad: item.cantidad,
        producto: { id: item.producto.id },
        adicional: item.adicionales && item.adicionales.length > 0
          ? { id_adicional: item.adicionales[0].id_adicional }
          : null,
        adicionales: (item.adicionales || []).map(a => ({
          id_adicional: a.id_adicional,
          cantidad: a.cantidad
        }))
      }))
    };

    this.enviandoPedido = true;
    this.pedidoService.agregarPedido(nuevoPedido).subscribe({
      next: (pedidoCreado) => {
        this.enviandoPedido = false;
        this.carritoService.vaciarCarrito();
        this.carritoService.notificarPedidoCreado();
        this.exitoPedido = `¡Pedido #${pedidoCreado.id_Pedido} realizado con éxito! Estado: ${pedidoCreado.estado}`;

        setTimeout(() => {
          this.cerrarCarrito();
          this.exitoPedido = '';
          this.router.navigate(['/clientes/perfil', cliente.clienteId]);
        }, 1200);
      },
      error: (err) => {
        console.error('Error al crear el pedido:', err);
        this.enviandoPedido = false;
        this.errorPedido = err?.error?.message || 'No se pudo procesar tu pedido. Verifica tu sesión e intenta de nuevo.';
      }
    });
  }
}
