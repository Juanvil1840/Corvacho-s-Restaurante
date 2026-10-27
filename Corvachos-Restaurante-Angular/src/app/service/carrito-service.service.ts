import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, EMPTY, Subject, catchError, concatMap } from 'rxjs';

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

  private readonly apiUrl = 'http://localhost:8090/api/carritos';

  private clienteSubject = new BehaviorSubject<Cliente | null>(null);

  cliente$ = this.clienteSubject.asObservable();

  private itemsSubject = new BehaviorSubject<ItemCarrito[]>([]);

  items$ = this.itemsSubject.asObservable();

  private abiertoSubject = new BehaviorSubject<boolean>(false);
  abierto$ = this.abiertoSubject.asObservable();

  private pedidoCreadoSubject = new Subject<void>();
  pedidoCreado$ = this.pedidoCreadoSubject.asObservable();

  private cambiosCarrito = new Subject<{ clienteId: number; items: ItemCarrito[] }>();
  private cargandoClienteId: number | null = null;

  constructor(private http: HttpClient) {
    this.cambiosCarrito.pipe(
      concatMap(({ clienteId, items }) =>
        this.http.put<{ items: any[] }>(
          `${this.apiUrl}/cliente/${clienteId}`,
          {
            items: items.map(item => ({
              productoId: item.producto.id,
              cantidad: item.cantidad,
              adicionales: item.adicionales.map(adicional => ({
                id_adicional: adicional.id_adicional,
                cantidad: adicional.cantidad
              }))
            }))
          }
        ).pipe(
          catchError(error => {
            console.error('No fue posible guardar el carrito:', error);
            return EMPTY;
          })
        )
      )
    ).subscribe();
  }

  private normalizarCliente(raw: any): Cliente | null {
    if (!raw || typeof raw !== 'object') {
      return null;
    }

    const id = Number(
      raw.clienteId ?? raw.id ?? raw.id_cliente ?? 0
    );

    if (!Number.isInteger(id) || id <= 0) {
      return null;
    }

    return {
      ...raw,
      clienteId: id
    };
  }

  setClienteLogueado(cliente: Cliente | null): void {
    const normalizado = this.normalizarCliente(cliente);

    if (!normalizado) {
      this.cerrarSesionCliente();
      return;
    }

    if (this.clienteSubject.value?.clienteId === normalizado.clienteId) {
      this.clienteSubject.next(normalizado);
      return;
    }

    const itemsPrevios = this.clienteSubject.value ? [] : this.itemsSubject.value;
    this.itemsSubject.next(itemsPrevios);
    this.clienteSubject.next(normalizado);
    this.cargandoClienteId = normalizado.clienteId;

    this.http.get<{ items: any[] }>(`${this.apiUrl}/cliente/${normalizado.clienteId}`).subscribe({
      next: respuesta => {
        if (this.clienteSubject.value?.clienteId !== normalizado.clienteId) {
          return;
        }
        const itemsGuardados = (respuesta.items || []).map(item => this.mapearItemBackend(item));
        const itemsPendientes = this.clienteSubject.value?.clienteId === normalizado.clienteId
          ? this.itemsSubject.value
          : itemsPrevios;
        const items = this.combinarItems(itemsGuardados, itemsPendientes);
        this.itemsSubject.next(items);
        this.cargandoClienteId = null;
        if (itemsPendientes.length > 0) {
          this.cambiosCarrito.next({ clienteId: normalizado.clienteId, items });
        }
      },
      error: error => {
        if (this.clienteSubject.value?.clienteId !== normalizado.clienteId) {
          return;
        }
        console.error('No fue posible recuperar el carrito:', error);
        this.cargandoClienteId = null;
        const itemsPendientes = this.itemsSubject.value.length > 0
          ? this.itemsSubject.value
          : itemsPrevios;
        this.itemsSubject.next(itemsPendientes);
        if (itemsPendientes.length > 0) {
          this.cambiosCarrito.next({ clienteId: normalizado.clienteId, items: itemsPendientes });
        }
      }
    });
  }

  getClienteLogueado(): Cliente | null {
    return this.clienteSubject.value;
  }

  cerrarSesionCliente(): void {
    this.clienteSubject.next(null);
    this.itemsSubject.next([]);
    this.cargandoClienteId = null;

    this.cerrarCarrito();
  }

  private guardarItems(
    items: ItemCarrito[]
  ): void {
    this.itemsSubject.next(items);
    if (this.clienteSubject.value && this.cargandoClienteId !== this.clienteSubject.value.clienteId) {
      this.cambiosCarrito.next({ clienteId: this.clienteSubject.value.clienteId, items });
    }
  }

  private mapearItemBackend(item: any): ItemCarrito {
    return {
      producto: {
        id: item.productoId,
        nombre: item.nombre,
        precio: item.precio,
        imagen: item.imagen || ''
      },
      cantidad: item.cantidad,
      adicionales: (item.adicionales || []).map((adicional: any) => ({
        id_adicional: adicional.id_adicional,
        nombre: adicional.nombre,
        precio: adicional.precio,
        cantidad: adicional.cantidad
      }))
    };
  }

  private combinarItems(base: ItemCarrito[], nuevos: ItemCarrito[]): ItemCarrito[] {
    const resultado = [...base];
    for (const nuevo of nuevos) {
      const firma = this.firmaAdicionales(nuevo.adicionales);
      const indice = resultado.findIndex(item =>
        item.producto.id === nuevo.producto.id && this.firmaAdicionales(item.adicionales) === firma
      );
      if (indice >= 0) {
        resultado[indice] = { ...resultado[indice], cantidad: resultado[indice].cantidad + nuevo.cantidad };
      } else {
        resultado.push(nuevo);
      }
    }
    return resultado;
  }

  private firmaAdicionales(
    adicionales: AdicionalCarrito[]
  ): string {
    if (!adicionales || adicionales.length === 0) {
      return 'none';
    }

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
    const cantValida =
      cantidad > 0 ? Math.floor(cantidad) : 1;

    const items = [...this.itemsSubject.value];

    let listaAdic: AdicionalCarrito[] = [];

    if (Array.isArray(adicionales)) {
      listaAdic = adicionales
        .filter(
          a =>
            a &&
            a.id_adicional > 0 &&
            a.cantidad > 0
        )
        .map(a => ({
          id_adicional: a.id_adicional,
          nombre: a.nombre,
          precio: a.precio || 0,
          cantidad: a.cantidad
        }));
    } else if (
      adicionales &&
      'id_adicional' in adicionales &&
      adicionales.id_adicional
    ) {
      listaAdic = [{
        id_adicional: adicionales.id_adicional,
        nombre: adicionales.nombre,
        precio: adicionales.precio || 0,
        cantidad: 1
      }];
    }

    const firmaNueva =
      this.firmaAdicionales(listaAdic);

    const existenteIdx = items.findIndex(
      i =>
        i.producto.id === producto.id &&
        this.firmaAdicionales(i.adicionales) === firmaNueva
    );

    if (existenteIdx >= 0) {
      items[existenteIdx] = {
        ...items[existenteIdx],
        cantidad:
          items[existenteIdx].cantidad + cantValida
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

    this.guardarItems(items);
  }

  modificarCantidad(
    index: number,
    delta: number
  ): void {
    const items = [...this.itemsSubject.value];

    if (index < 0 || index >= items.length) {
      return;
    }

    const nuevaCant =
      items[index].cantidad + delta;

    if (nuevaCant <= 0) {
      items.splice(index, 1);
    } else {
      items[index] = {
        ...items[index],
        cantidad: nuevaCant
      };
    }

    this.guardarItems(items);
  }

  eliminarItem(index: number): void {
    const items = [...this.itemsSubject.value];

    if (index < 0 || index >= items.length) {
      return;
    }

    items.splice(index, 1);

    this.guardarItems(items);
  }

  vaciarCarrito(): void {
    this.guardarItems([]);
  }

  calcularPrecioUnitarioItem(
    item: ItemCarrito
  ): number {
    const base = item.producto.precio || 0;

    const extras = (item.adicionales || []).reduce(
      (sum, a) =>
        sum +
        (a.precio || 0) * (a.cantidad || 1),
      0
    );

    return base + extras;
  }

  getTotalCantidad(): number {
    return this.itemsSubject.value.reduce(
      (acc, item) => acc + item.cantidad,
      0
    );
  }

  getTotalPrecio(): number {
    return this.itemsSubject.value.reduce(
      (acc, item) =>
        acc +
        this.calcularPrecioUnitarioItem(item) *
        item.cantidad,
      0
    );
  }

  abrirCarrito(): void {
    this.abiertoSubject.next(true);
  }

  cerrarCarrito(): void {
    this.abiertoSubject.next(false);
  }

  toggleCarrito(): void {
    this.abiertoSubject.next(
      !this.abiertoSubject.value
    );
  }

  notificarPedidoCreado(): void {
    this.pedidoCreadoSubject.next();
  }

}
