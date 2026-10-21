import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { Carrito } from '../models/carrito.model';
import { Producto } from '../models/producto.model';
import { Adicional } from '../models/adicional.model';
import { Cliente } from '../models/cliente.model';

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

@Injectable({
  providedIn: 'root'
})
export class CarritoServiceService {

  private readonly STORAGE_KEY_CARRITO = 'corvachos_carrito';
  private readonly STORAGE_KEY_CLIENTE = 'cliente';

  private itemsSubject = new BehaviorSubject<ItemCarrito[]>(this.cargarItemsDeStorage());
  items$ = this.itemsSubject.asObservable();

  private abiertoSubject = new BehaviorSubject<boolean>(false);
  abierto$ = this.abiertoSubject.asObservable();

  private clienteSubject = new BehaviorSubject<Cliente | null>(this.cargarClienteDeStorage());
  cliente$ = this.clienteSubject.asObservable();

  private pedidoCreadoSubject = new Subject<void>();
  pedidoCreado$ = this.pedidoCreadoSubject.asObservable();

  private carritos: Carrito[] = [];

  constructor() {}

  // --- Gestión de sesión del cliente ---
  private normalizarCliente(rawObj: any): Cliente | null {
    if (!rawObj || typeof rawObj !== 'object') return null;
    const cid = Number(rawObj.clienteId ?? rawObj.id ?? rawObj.id_cliente ?? 0);
    return {
      ...rawObj,
      clienteId: cid > 0 ? cid : 0
    };
  }

  private cargarClienteDeStorage(): Cliente | null {
    try {
      const raw = localStorage.getItem(this.STORAGE_KEY_CLIENTE);
      return raw ? this.normalizarCliente(JSON.parse(raw)) : null;
    } catch {
      return null;
    }
  }

  setClienteLogueado(cliente: Cliente | null): void {
    const normalizado = this.normalizarCliente(cliente);
    if (normalizado) {
      localStorage.setItem(this.STORAGE_KEY_CLIENTE, JSON.stringify(normalizado));
    } else {
      localStorage.removeItem(this.STORAGE_KEY_CLIENTE);
    }
    this.clienteSubject.next(normalizado);
  }

  getClienteLogueado(): Cliente | null {
    const actual = this.clienteSubject.value;
    if (actual && actual.clienteId > 0) return actual;
    const deStorage = this.cargarClienteDeStorage();
    if (deStorage) {
      this.clienteSubject.next(deStorage);
    }
    return deStorage;
  }

  cerrarSesionCliente(): void {
    localStorage.removeItem(this.STORAGE_KEY_CLIENTE);
    this.clienteSubject.next(null);
    this.vaciarCarrito();
    this.cerrarCarrito();
  }

  // --- Gestión de ítems del carrito ---
  private cargarItemsDeStorage(): ItemCarrito[] {
    try {
      const raw = localStorage.getItem(this.STORAGE_KEY_CARRITO);
      if (!raw) return [];
      const parsed = JSON.parse(raw);
      if (!Array.isArray(parsed)) return [];
      return parsed.map((item: any) => {
        let adics: AdicionalCarrito[] = [];
        if (Array.isArray(item.adicionales)) {
          adics = item.adicionales.map((a: any) => ({
            id_adicional: a.id_adicional,
            nombre: a.nombre,
            precio: a.precio || 0,
            cantidad: a.cantidad > 0 ? a.cantidad : 1
          }));
        } else if (item.adicional && item.adicional.id_adicional) {
          adics = [{
            id_adicional: item.adicional.id_adicional,
            nombre: item.adicional.nombre,
            precio: item.adicional.precio || 0,
            cantidad: 1
          }];
        }
        return {
          producto: {
            id: item.producto?.id,
            nombre: item.producto?.nombre,
            precio: item.producto?.precio || 0,
            imagen: item.producto?.imagen || ''
          },
          cantidad: item.cantidad > 0 ? item.cantidad : 1,
          adicionales: adics
        };
      });
    } catch {
      return [];
    }
  }

  private guardarItemsEnStorage(items: ItemCarrito[]): void {
    localStorage.setItem(this.STORAGE_KEY_CARRITO, JSON.stringify(items));
    this.itemsSubject.next(items);
  }

  private firmaAdicionales(adicionales: AdicionalCarrito[]): string {
    if (!adicionales || adicionales.length === 0) return 'none';
    return [...adicionales]
      .sort((a, b) => a.id_adicional - b.id_adicional)
      .map(a => `${a.id_adicional}x${a.cantidad}`)
      .join('|');
  }

  getItems(): ItemCarrito[] {
    return this.itemsSubject.value;
  }

  agregarItem(
    producto: Producto | ProductoResumenCarrito,
    cantidad: number = 1,
    adicionales: AdicionalCarrito[] | Adicional | null = null
  ): void {
    const cantValida = cantidad > 0 ? Math.floor(cantidad) : 1;
    const items = [...this.itemsSubject.value];

    let listaAdic: AdicionalCarrito[] = [];
    if (Array.isArray(adicionales)) {
      listaAdic = adicionales
        .filter(a => a && a.id_adicional > 0 && a.cantidad > 0)
        .map(a => ({
          id_adicional: a.id_adicional,
          nombre: a.nombre,
          precio: a.precio || 0,
          cantidad: a.cantidad
        }));
    } else if (adicionales && 'id_adicional' in adicionales && adicionales.id_adicional) {
      listaAdic = [{
        id_adicional: adicionales.id_adicional,
        nombre: adicionales.nombre,
        precio: adicionales.precio || 0,
        cantidad: 1
      }];
    }

    const firmaNueva = this.firmaAdicionales(listaAdic);

    const existenteIdx = items.findIndex(
      i => i.producto.id === producto.id && this.firmaAdicionales(i.adicionales) === firmaNueva
    );

    if (existenteIdx >= 0) {
      items[existenteIdx] = {
        ...items[existenteIdx],
        cantidad: items[existenteIdx].cantidad + cantValida
      };
    } else {
      items.push({
        producto: {
          id: producto.id,
          nombre: producto.nombre,
          precio: producto.precio || 0,
          imagen: producto.imagen || ''
        },
        cantidad: cantValida,
        adicionales: listaAdic
      });
    }

    this.guardarItemsEnStorage(items);
  }

  modificarCantidad(index: number, delta: number): void {
    const items = [...this.itemsSubject.value];
    if (index < 0 || index >= items.length) return;

    const nuevaCant = items[index].cantidad + delta;
    if (nuevaCant <= 0) {
      items.splice(index, 1);
    } else {
      items[index] = { ...items[index], cantidad: nuevaCant };
    }

    this.guardarItemsEnStorage(items);
  }

  eliminarItem(index: number): void {
    const items = [...this.itemsSubject.value];
    if (index < 0 || index >= items.length) return;
    items.splice(index, 1);
    this.guardarItemsEnStorage(items);
  }

  vaciarCarrito(): void {
    localStorage.removeItem(this.STORAGE_KEY_CARRITO);
    this.itemsSubject.next([]);
  }

  calcularPrecioUnitarioItem(item: ItemCarrito): number {
    const base = item.producto.precio || 0;
    const extras = (item.adicionales || []).reduce(
      (sum, a) => sum + (a.precio || 0) * (a.cantidad || 1),
      0
    );
    return base + extras;
  }

  getTotalCantidad(): number {
    return this.itemsSubject.value.reduce((acc, item) => acc + item.cantidad, 0);
  }

  getTotalPrecio(): number {
    return this.itemsSubject.value.reduce((acc, item) => {
      return acc + this.calcularPrecioUnitarioItem(item) * item.cantidad;
    }, 0);
  }

  // --- Visibilidad del drawer del carrito ---
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

  getCarritos(): Carrito[] {
    return this.carritos;
  }
}
