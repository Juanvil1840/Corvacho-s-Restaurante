import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { CarritoService, ItemCarrito } from '../../service/carrito.service';
import { PedidoService } from '../../service/pedido.service';
import { Cliente } from '../../models/cliente.model';

// Barra de navegación principal que incluye los enlaces de acceso, estado de sesión y el panel del carrito de compras
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit, OnDestroy {

  // Indicadores de la ruta actual para adaptar la vista de la barra de navegación
  esPaginaLogin = false;
  esPortalOperario = false;
  esPortalAdmin = false; 
  
  // Estado de sesión para usuarios administrativos y operarios
  adminLogueado = false;
  operarioLogueado = false;
  
  // Datos reactivos del cliente autenticado y su carrito de compras
  clienteLogueado: Cliente | null = null;
  itemsCarrito: ItemCarrito[] = [];
  carritoAbierto = false;

  // Estados de carga y mensajes al confirmar un pedido desde el carrito
  enviandoPedido = false;
  errorPedido = '';
  exitoPedido = '';

  // Suscripciones activas para liberarlas al destruir el componente
  private subs: Subscription[] = [];

  constructor(
    private router: Router,
    private carritoService: CarritoService,
    private pedidoService: PedidoService
  ) {}

  // Se suscribe a los cambios de ruta, sesión del cliente y estado del carrito al iniciar
  ngOnInit(): void {
    this.actualizarEstadoRuta(this.router.url);

    this.subs.push(
      this.router.events.pipe(
        filter(event => event instanceof NavigationEnd)
      ).subscribe(event => {
        this.actualizarEstadoRuta((event as NavigationEnd).urlAfterRedirects);
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

  // Cancela todas las suscripciones al destruir el componente
  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
  }

  // Actualiza las banderas de vista y sesión según la URL actual
  private actualizarEstadoRuta(url: string): void {
    this.esPaginaLogin = url === '/login' || url.startsWith('/login?');
    this.esPortalOperario = url.startsWith('/portal-operario') || url.startsWith('/login-operario');
    this.esPortalAdmin = url.startsWith('/login-admin') || url.startsWith('/portal-admin');
    this.adminLogueado = !!localStorage.getItem('administrador');
    this.operarioLogueado = !!localStorage.getItem('operador');
  }

  // Cantidad total de productos en el carrito
  get totalCantidad(): number {
    return this.carritoService.getTotalCantidad();
  }

  // Precio total acumulado del carrito
  get totalPrecio(): number {
    return this.carritoService.getTotalPrecio();
  }

  // Calcula el precio unitario de un ítem sumando sus adicionales
  calcularUnitario(item: ItemCarrito): number {
    return this.carritoService.calcularPrecioUnitarioItem(item);
  }

  // Abre o cierra el panel lateral del carrito limpiando mensajes previos
  toggleCarrito(): void {
    this.exitoPedido = '';
    this.errorPedido = '';
    this.carritoService.toggleCarrito();
  }

  // Cierra el panel lateral del carrito
  cerrarCarrito(): void {
    this.carritoService.cerrarCarrito();
  }

  // Cierra el carrito y navega a la página del menú
  irAlMenu(): void {
    this.cerrarCarrito();
    this.router.navigate(['/menu']);
  }

  // Redirige al perfil del cliente si hay sesión activa o al login si no ha iniciado sesión
  irAlPerfil(): void {
    this.cerrarCarrito();
    const cliente = this.carritoService.getClienteLogueado();
    if (cliente && cliente.clienteId > 0) {
      this.router.navigate(['/clientes/perfil', cliente.clienteId]);
    } else {
      this.router.navigate(['/login']);
    }
  }

  // Cambia la cantidad de un producto dentro del carrito
  modificarCantidad(index: number, cambio: number): void {
    this.carritoService.modificarCantidad(index, cambio);
  }

  // Elimina un producto del carrito según su índice
  eliminarItem(index: number): void {
    this.carritoService.eliminarItem(index);
  }

  // Vacía todos los elementos del carrito
  vaciarCarrito(): void {
    this.carritoService.vaciarCarrito();
  }

  // Valida la sesión y los productos del carrito para registrar un nuevo pedido en el backend
  confirmarPedido(): void {
    this.errorPedido = '';
    this.exitoPedido = '';

    // Verifica que el carrito tenga al menos un producto
    if (this.itemsCarrito.length === 0) {
      this.errorPedido = 'Tu carrito está vacío. Añade comidas desde el menú.';
      return;
    }

    // Verifica que el cliente haya iniciado sesión antes de confirmar el pedido
    const cliente = this.carritoService.getClienteLogueado();
    if (!cliente || !cliente.clienteId) {
      this.cerrarCarrito();
      this.router.navigate(['/login']);
      return;
    }

    // Solo se envian ids y cantidades, el backend busca los precios
    const nuevoPedido = {
      cliente: { clienteId: cliente.clienteId },
      detallePedidos: this.itemsCarrito.map(item => ({
        cantidad: item.cantidad,
        producto: { id: item.producto.id },
        adicionales: item.adicionales.map(a => ({
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

        // Cierra el carrito después de unos instantes y lleva al cliente a su perfil
        setTimeout(() => {
          this.cerrarCarrito();
          this.exitoPedido = '';
          this.router.navigate(['/clientes/perfil', cliente.clienteId]);
        }, 1200);
      },
      error: (err) => {
        console.error('Error al crear el pedido:', err);
        this.enviandoPedido = false;
        this.errorPedido = err?.error?.mensaje || 'No se pudo procesar tu pedido. Verifica tu sesión e intenta de nuevo.';
      }
    });
  }
}
