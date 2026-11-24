import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { CarritoService, ItemCarrito } from '../../service/carrito.service';
import { PedidoService } from '../../service/pedido.service';
import { AuthService } from '../../service/auth.service';
import { ConfetiService } from '../../service/confeti.service';
import { Cliente } from '../../models/cliente.model';
import { CuponValidado } from '../../models/cupon.model';
import { CarritoCuponComponent } from '../carrito-cupon/carrito-cupon.component';

// Estructura del descuento obtenido en la ruleta de bienvenida al registrarse
interface DescuentoBienvenidaGuardado {
  porcentaje: number;
  codigo?: string;
  correo: string;
  usado?: boolean;
}

// Barra de navegación principal que incluye los enlaces de acceso, estado de sesión y el panel del carrito de compras
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, CarritoCuponComponent],
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

  // Cupón de descuento aplicado en el carrito (el backend lo vuelve a validar al crear el pedido)
  cuponAplicado: CuponValidado | null = null;

  // Clave en localStorage donde se guarda el descuento de bienvenida de la ruleta
  private readonly claveDescuentoBienvenida = 'corvachos_descuento_bienvenida';

  // Suscripciones activas para liberarlas al destruir el componente
  private subs: Subscription[] = [];

  constructor(
    private router: Router,
    private carritoService: CarritoService,
    private pedidoService: PedidoService,
    private authService: AuthService,
    private confetiService: ConfetiService
  ) {}

  // Se suscribe a los cambios de ruta, sesión del cliente y estado del carrito al iniciar
  ngOnInit(): void {
    this.actualizarEstadoRuta(this.router.url);

    this.subs.push(
      this.router.events.pipe(
        filter(event => event instanceof NavigationEnd)
      ).subscribe(event => {
        this.actualizarEstadoRuta((event as NavigationEnd).urlAfterRedirects);
        this.cerrarMenuMovil();
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
        if (items.length === 0) {
          this.cuponAplicado = null;
        }
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
  // En celular el menú desplegable de Bootstrap queda abierto al cambiar de página; aquí se cierra
  private cerrarMenuMovil(): void {
    const menu = document.getElementById('navbarNav');
    if (!menu?.classList.contains('show')) {
      return;
    }
    const bootstrap = (window as any).bootstrap;
    if (bootstrap?.Collapse) {
      bootstrap.Collapse.getOrCreateInstance(menu, { toggle: false }).hide();
    } else {
      menu.classList.remove('show');
    }
  }

  private actualizarEstadoRuta(url: string): void {
    this.esPaginaLogin = url === '/login' || url.startsWith('/login?');
    this.esPortalOperario = url.startsWith('/portal-operario') || url.startsWith('/login-operario');
    this.esPortalAdmin = url.startsWith('/login-admin') || url.startsWith('/portal-admin');
    // El rol sale del token JWT (si no hay token o ya venció no hay sesión)
    const rol = this.authService.getRol();
    this.adminLogueado = rol === 'ADMIN';
    this.operarioLogueado = rol === 'OPERADOR';
  }

  // Cierra la sesión de cualquier rol (borra el token JWT) y vuelve al inicio
  cerrarSesion(): void {
    this.cerrarCarrito();
    this.authService.cerrarSesion();
    this.adminLogueado = false;
    this.operarioLogueado = false;
    this.router.navigate(['/']);
  }

  // Cantidad total de productos en el carrito
  get totalCantidad(): number {
    return this.carritoService.getTotalCantidad();
  }

  // Precio total acumulado del carrito
  get totalPrecio(): number {
    return this.carritoService.getTotalPrecio();
  }

  // Cupón del premio de la ruleta de bienvenida (RULETA10 o RULETA5) si el cliente aún no lo ha usado.
  // Se aplica solo en el carrito y el backend lo cobra como cualquier cupón, así el pedido guarda el descuento
  get codigoBienvenida(): string | null {
    const dato = this.obtenerDescuentoBienvenidaActivo();
    return dato ? (dato.codigo || 'RULETA' + dato.porcentaje) : null;
  }

  // Calcula el precio unitario de un ítem sumando sus adicionales
  // Lo que se paga: el total menos el descuento del cupón (el premio de la ruleta también llega como cupón)
  get totalAPagar(): number {
    const descCupon = this.cuponAplicado?.descuento ?? 0;
    return Math.max(0, this.totalPrecio - descCupon);
  }

  // Recupera el descuento de bienvenida si existe, no ha sido usado y corresponde al cliente actual
  private obtenerDescuentoBienvenidaActivo(): DescuentoBienvenidaGuardado | null {
    try {
      const raw = localStorage.getItem(this.claveDescuentoBienvenida);
      if (!raw) {
        return null;
      }
      const parsed: DescuentoBienvenidaGuardado = JSON.parse(raw);
      if (!parsed || parsed.usado || !parsed.porcentaje) {
        return null;
      }
      const correoActual = (this.clienteLogueado?.correo ?? '').trim().toLowerCase();
      if (correoActual && parsed.correo && correoActual !== parsed.correo.trim().toLowerCase()) {
        return null;
      }
      return parsed;
    } catch {
      return null;
    }
  }

  // Marca el descuento de bienvenida como usado al confirmar el pedido para que no se repita
  private marcarDescuentoBienvenidaUsado(): void {
    const activo = this.obtenerDescuentoBienvenidaActivo();
    if (activo) {
      localStorage.setItem(
        this.claveDescuentoBienvenida,
        JSON.stringify({ ...activo, usado: true })
      );
    }
  }

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
      codigoCupon: this.cuponAplicado?.codigo ?? null,
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
        // Lanza la celebración de confeti y consume el descuento de bienvenida al confirmar con éxito
        this.confetiService.lanzarCelebracion();
        if (this.cuponAplicado && this.cuponAplicado.codigo === this.codigoBienvenida) {
          this.marcarDescuentoBienvenidaUsado();
        }

        this.enviandoPedido = false;
        this.cuponAplicado = null;
        this.carritoService.vaciarCarrito();
        this.carritoService.notificarPedidoCreado();
        this.exitoPedido = `¡Pedido #${pedidoCreado.id_Pedido} realizado con éxito! Estado: ${pedidoCreado.estado}`;

        // Cierra el carrito después de unos instantes y lleva al cliente al detalle de su pedido, que se actualiza en vivo
        setTimeout(() => {
          this.cerrarCarrito();
          this.exitoPedido = '';
          this.router.navigate(['/pedidos/detalle', pedidoCreado.id_Pedido]);
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