import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Subject } from 'rxjs';
import { Producto } from '../models/producto.model';
import { Cliente } from '../models/cliente.model';
import { environment } from '../../environments/environment';

// Estructura de un adicional dentro del carrito: id, nombre, precio y cantidad
export interface AdicionalCarrito {
  id_adicional: number;
  nombre: string;
  precio: number;
  cantidad: number;
}

// Estructura de un producto resumido dentro del carrito: id, nombre, precio e imagen
export interface ProductoResumenCarrito {
  id: number;
  nombre: string;
  precio: number;
  imagen: string;
}

// Estructura de un item del carrito: producto, cantidad y adicionales
export interface ItemCarrito {
  producto: ProductoResumenCarrito;
  cantidad: number;
  adicionales: AdicionalCarrito[];
}

// Estructura de un producto 
interface ProductoCarritoBackend {
  productoId: number;
  nombre?: string;
  precio?: number;
  imagen?: string;
  cantidad: number;
  adicionales: AdicionalCarrito[];
}

// Estructura del carrito completo 
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

  // Indica si el panel del carrito esta abierto
  private abiertoSubject = new BehaviorSubject<boolean>(false);
  abierto$ = this.abiertoSubject.asObservable();

  // Notifica al perfil que se creo un pedido para recargar el historial
  private pedidoCreadoSubject = new Subject<void>();
  pedidoCreado$ = this.pedidoCreadoSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Guarda el cliente logueado y sincroniza su carrito con el backend
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

    // Si habia items sin sesion, se fusionan con el carrito guardado
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

  // Devuelve el cliente logueado actualmente
  getClienteLogueado(): Cliente | null {
    return this.clienteSubject.value;
  }

  // Cierra la sesion del cliente y vacia el carrito
  cerrarSesionCliente(): void {
    this.clienteSubject.next(null);
    this.itemsSubject.next([]);
    this.cerrarCarrito();
  }

  // Devuelve los items actuales del carrito
  getItems(): ItemCarrito[] {
    return this.itemsSubject.value;
  }

  // Agrega un producto al carrito
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

  // Modifica la cantidad de un item 
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

  // Elimina un item del carrito por su indice
  eliminarItem(index: number): void {
    const items = [...this.itemsSubject.value];
    items.splice(index, 1);
    this.guardarItems(items);
  }

  // Vacia todo el carrito
  vaciarCarrito(): void {
    this.guardarItems([]);
  }

  // Calcula el precio de una unidad de un item incluyendo sus adicionales
  calcularPrecioUnitarioItem(item: ItemCarrito): number {
    let total = item.producto.precio;
    for (const adicional of item.adicionales) {
      total += adicional.precio * adicional.cantidad;
    }
    return total;
  }

  // Devuelve la cantidad total de productos en el carrito
  getTotalCantidad(): number {
    let total = 0;
    for (const item of this.itemsSubject.value) {
      total += item.cantidad;
    }
    return total;
  }

  // Devuelve el precio total de todos los items del carrito
  getTotalPrecio(): number {
    let total = 0;
    for (const item of this.itemsSubject.value) {
      total += this.calcularPrecioUnitarioItem(item) * item.cantidad;
    }
    return total;
  }

  // Abre el panel del carrito
  abrirCarrito(): void {
    this.abiertoSubject.next(true);
  }

  // Cierra el panel del carrito
  cerrarCarrito(): void {
    this.abiertoSubject.next(false);
  }

  // Alterna el estado del panel del carrito
  toggleCarrito(): void {
    this.abiertoSubject.next(!this.abiertoSubject.value);
  }

  // Notifica que se creo un pedido
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

  // Guarda el carrito en el backend
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

  // Si el producto ya existe con los mismos adicionales suma la cantidad, si no lo agrega
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

  // Compara dos listas de adicionales para saber si son iguales
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

  // Convierte un item del backend al formato del frontend
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