import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Subject } from 'rxjs';
import { Producto } from '../models/producto.model';
import { Cliente } from '../models/cliente.model';
import { environment } from '../../environments/environment';

export interface AdicionalCarrito {
  id_adicional: number;
  nombre: string;
  precio: number;
  cantidad: number;
}

export interface ProductoResumenCarrito {
  id: number;
  nombre: string;
  precio: number;
  imagen: string;
}

export interface ItemCarrito {
  producto: ProductoResumenCarrito;
  cantidad: number;
  adicionales: AdicionalCarrito[];
}

// Asi llega y se envia el carrito al backend (CarritoDTO)
interface ProductoCarritoBackend {
  productoId: number;
  nombre?: string;
  precio?: number;
  imagen?: string;
  cantidad: number;
  adicionales: AdicionalCarrito[];
}

interface CarritoBackend {
  items: ProductoCarritoBackend[];
}

@Injectable({
  providedIn: 'root'
})
export class CarritoService {

  private apiUrl = `${environment.apiUrl}/carritos`;

  // Cliente con sesion iniciada
  private clienteSubject = new BehaviorSubject<Cliente | null>(null);
  cliente$ = this.clienteSubject.asObservable();

  // Productos del carrito
  private itemsSubject = new BehaviorSubject<ItemCarrito[]>([]);
  items$ = this.itemsSubject.asObservable();

  // Si el panel del carrito esta abierto
  private abiertoSubject = new BehaviorSubject<boolean>(false);
  abierto$ = this.abiertoSubject.asObservable();

  // Avisa al perfil que se creo un pedido para recargar el historial
  private pedidoCreadoSubject = new Subject<void>();
  pedidoCreado$ = this.pedidoCreadoSubject.asObservable();

  constructor(private http: HttpClient) {}

  setClienteLogueado(cliente: Cliente | null): void {
    if (!cliente || !cliente.clienteId) {
      this.cerrarSesionCliente();
      return;
    }

    // Si es el mismo cliente solo se actualizan sus datos
    if (this.clienteSubject.value?.clienteId === cliente.clienteId) {
      this.clienteSubject.next(cliente);
      return;
    }

    // Lo que el usuario agrego antes de iniciar sesion se suma a su carrito guardado
    const itemsSinSesion = this.clienteSubject.value ? [] : this.itemsSubject.value;
    this.clienteSubject.next(cliente);
    this.itemsSubject.next([]);

    this.http.get<CarritoBackend>(`${this.apiUrl}/cliente/${cliente.clienteId}`).subscribe({
      next: (respuesta) => {
        let items = respuesta.items.map(item => this.convertirItem(item));
        for (const item of itemsSinSesion) {
          items = this.sumarItem(items, item);
        }
        this.itemsSubject.next(items);
        if (itemsSinSesion.length > 0) {
          this.guardarEnBackend(items);
        }
      },
      error: (error) => {
        console.error('No fue posible recuperar el carrito:', error);
        this.itemsSubject.next(itemsSinSesion);
      }
    });
  }

  getClienteLogueado(): Cliente | null {
    return this.clienteSubject.value;
  }

  cerrarSesionCliente(): void {
    this.clienteSubject.next(null);
    this.itemsSubject.next([]);
    this.cerrarCarrito();
  }

  getItems(): ItemCarrito[] {
    return this.itemsSubject.value;
  }

  agregarItem(producto: Producto, cantidad: number = 1, adicionales: AdicionalCarrito[] | null = null): void {
    const nuevo: ItemCarrito = {
      producto: {
        id: producto.id,
        nombre: producto.nombre,
        precio: producto.precio,
        imagen: producto.imagen || ''
      },
      cantidad: cantidad > 0 ? cantidad : 1,
      adicionales: (adicionales || []).filter(a => a.cantidad > 0)
    };
    this.guardarItems(this.sumarItem(this.itemsSubject.value, nuevo));
  }

  modificarCantidad(index: number, cambio: number): void {
    const items = [...this.itemsSubject.value];
    if (!items[index]) {
      return;
    }
    const nuevaCantidad = items[index].cantidad + cambio;
    if (nuevaCantidad <= 0) {
      items.splice(index, 1);
    } else {
      items[index] = { ...items[index], cantidad: nuevaCantidad };
    }
    this.guardarItems(items);
  }

  eliminarItem(index: number): void {
    const items = [...this.itemsSubject.value];
    items.splice(index, 1);
    this.guardarItems(items);
  }

  vaciarCarrito(): void {
    this.guardarItems([]);
  }

  // Precio de una unidad del producto con sus adicionales
  calcularPrecioUnitarioItem(item: ItemCarrito): number {
    let total = item.producto.precio;
    for (const adicional of item.adicionales) {
      total += adicional.precio * adicional.cantidad;
    }
    return total;
  }

  getTotalCantidad(): number {
    let total = 0;
    for (const item of this.itemsSubject.value) {
      total += item.cantidad;
    }
    return total;
  }

  getTotalPrecio(): number {
    let total = 0;
    for (const item of this.itemsSubject.value) {
      total += this.calcularPrecioUnitarioItem(item) * item.cantidad;
    }
    return total;
  }

  abrirCarrito(): void {
    this.abiertoSubject.next(true);
  }

  cerrarCarrito(): void {
    this.abiertoSubject.next(false);
  }

  toggleCarrito(): void {
    this.abiertoSubject.next(!this.abiertoSubject.value);
  }

  notificarPedidoCreado(): void {
    this.pedidoCreadoSubject.next();
  }

  // Actualiza la lista y, si hay sesion, la guarda en el backend
  private guardarItems(items: ItemCarrito[]): void {
    this.itemsSubject.next(items);
    if (this.clienteSubject.value) {
      this.guardarEnBackend(items);
    }
  }

  private guardarEnBackend(items: ItemCarrito[]): void {
    const cliente = this.clienteSubject.value;
    if (!cliente) {
      return;
    }
    const carrito: CarritoBackend = {
      items: items.map(item => ({
        productoId: item.producto.id,
        cantidad: item.cantidad,
        adicionales: item.adicionales
      }))
    };
    this.http.put<CarritoBackend>(`${this.apiUrl}/cliente/${cliente.clienteId}`, carrito).subscribe({
      error: (error) => console.error('No fue posible guardar el carrito:', error)
    });
  }

  // Si ya existe el mismo producto con los mismos adicionales se suma la cantidad,
  // si no, se agrega como un item nuevo
  private sumarItem(items: ItemCarrito[], nuevo: ItemCarrito): ItemCarrito[] {
    const resultado = [...items];
    const index = resultado.findIndex(item =>
      item.producto.id === nuevo.producto.id && this.mismosAdicionales(item.adicionales, nuevo.adicionales)
    );
    if (index >= 0) {
      resultado[index] = { ...resultado[index], cantidad: resultado[index].cantidad + nuevo.cantidad };
    } else {
      resultado.push(nuevo);
    }
    return resultado;
  }

  private mismosAdicionales(a: AdicionalCarrito[], b: AdicionalCarrito[]): boolean {
    if (a.length !== b.length) {
      return false;
    }
    return a.every(adicionalA =>
      b.some(adicionalB =>
        adicionalB.id_adicional === adicionalA.id_adicional && adicionalB.cantidad === adicionalA.cantidad
      )
    );
  }

  private convertirItem(item: ProductoCarritoBackend): ItemCarrito {
    return {
      producto: {
        id: item.productoId,
        nombre: item.nombre || '',
        precio: item.precio || 0,
        imagen: item.imagen || ''
      },
      cantidad: item.cantidad,
      adicionales: item.adicionales || []
    };
  }
}
