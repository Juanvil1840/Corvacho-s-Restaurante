import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { Carrito } from '../models/carrito.model';
import { Producto } from '../models/producto.model';
import { Adicional } from '../models/adicional.model';
import { Cliente } from '../models/cliente.model';

// Define la estructura de un adicional seleccionado para tipar sus datos en el carrito y calcular su costo individual.
export interface AdicionalCarrito {
  id_adicional: number;
  nombre: string;
  precio: number;
  cantidad: number;
}

// Define los datos resumidos del plato para evitar guardar objetos pesados en el carrito y optimizar el rendimiento.
export interface ProductoResumenCarrito {
  id: number;
  nombre: string;
  precio: number;
  imagen: string;
}

// Agrupa un producto con sus cantidades y adicionales para representar una fila completa de compra y gestionarla en el pedido.
export interface ItemCarrito {
  producto: ProductoResumenCarrito;
  cantidad: number;
  adicionales: AdicionalCarrito[];
}

@Injectable({
  providedIn: 'root'
})
export class CarritoServiceService {

  // Define las claves de texto para identificar los datos del carrito y del cliente en el almacenamiento del navegador (localStorage).
  private readonly STORAGE_KEY_CARRITO = 'corvachos_carrito';
  private readonly STORAGE_KEY_CLIENTE = 'cliente';

  // Crea un BehaviorSubject de RxJS con los ítems del carrito para emitir los cambios en tiempo real a los componentes y recordar el último estado.
  private itemsSubject = new BehaviorSubject<ItemCarrito[]>(this.cargarItemsDeStorage());
  // Expone los ítems como un Observable de solo lectura para permitir que el Navbar se suscriba y evitar que componentes externos modifiquen la lista directamente.
  items$ = this.itemsSubject.asObservable();

  // Maneja el estado booleano de visibilidad para controlar si el panel lateral (drawer) está abierto o cerrado y sincronizarlo en la pantalla.
  private abiertoSubject = new BehaviorSubject<boolean>(false);
  // Expone el estado de apertura como Observable para que el Navbar reaccione y anime la apertura o cierre del panel lateral.
  abierto$ = this.abiertoSubject.asObservable();

  // Almacena el cliente autenticado en memoria para saber quién está comprando y persistir su sesión activa.
  private clienteSubject = new BehaviorSubject<Cliente | null>(this.cargarClienteDeStorage());
  // Expone el cliente logueado como Observable para actualizar el Navbar según el usuario conectado y ocultar o mostrar opciones de perfil.
  cliente$ = this.clienteSubject.asObservable();

  // Crea un Subject tipo señal para emitir un evento cuando se confirma un pedido y avisar a la pantalla de perfil que recargue su tabla de historial.
  private pedidoCreadoSubject = new Subject<void>();
  // Expone el evento de creación como Observable para que otros componentes escuchen cuando se finalizó una orden y refresquen sus vistas.
  pedidoCreado$ = this.pedidoCreadoSubject.asObservable();

  // Mantiene una lista interna de carritos para compatibilidad con modelos antiguos y no romper referencias previas.
  private carritos: Carrito[] = [];

  constructor() {}

  // --- Gestión de sesión del cliente ---

  // Estandariza los nombres de propiedades del cliente para resolver discrepancias entre clienteId, id o id_cliente y devolver siempre un objeto consistente.
  private normalizarCliente(rawObj: any): Cliente | null {
    if (!rawObj || typeof rawObj !== 'object') return null;
    const cid = Number(rawObj.clienteId ?? rawObj.id ?? rawObj.id_cliente ?? 0);
    return {
      ...rawObj,
      clienteId: cid > 0 ? cid : 0
    };
  }

  // Lee el cliente guardado en localStorage para restaurar la sesión cuando el usuario recarga la página (F5) y evitar que tenga que volver a iniciar sesión.
  private cargarClienteDeStorage(): Cliente | null {
    try {
      const raw = localStorage.getItem(this.STORAGE_KEY_CLIENTE);
      return raw ? this.normalizarCliente(JSON.parse(raw)) : null;
    } catch {
      return null;
    }
  }

  // Guarda el cliente autenticado en localStorage y emite el valor por RxJS para mantener la sesión activa entre recargas y notificar a los componentes de la interfaz.
  setClienteLogueado(cliente: Cliente | null): void {
    const normalizado = this.normalizarCliente(cliente);
    if (normalizado) {
      localStorage.setItem(this.STORAGE_KEY_CLIENTE, JSON.stringify(normalizado));
    } else {
      localStorage.removeItem(this.STORAGE_KEY_CLIENTE);
    }
    this.clienteSubject.next(normalizado);
  }

  // Obtiene el cliente actualmente en memoria o lo busca en el almacenamiento local para entregar los datos del usuario logueado y verificar si puede comprar.
  getClienteLogueado(): Cliente | null {
    const actual = this.clienteSubject.value;
    if (actual && actual.clienteId > 0) return actual;
    const deStorage = this.cargarClienteDeStorage();
    if (deStorage) {
      this.clienteSubject.next(deStorage);
    }
    return deStorage;
  }

  // Elimina el cliente de localStorage y vacía el carrito para cerrar la sesión de forma segura y evitar que el siguiente usuario vea datos ajenos.
  cerrarSesionCliente(): void {
    localStorage.removeItem(this.STORAGE_KEY_CLIENTE);
    this.clienteSubject.next(null);
    this.vaciarCarrito();
    this.cerrarCarrito();
  }

  // --- Gestión de ítems del carrito ---

  // Recupera los productos guardados en localStorage al iniciar la app para que el usuario no pierda lo que agregó si recarga la página y estructurar los adicionales correctamente.
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

  // Almacena la lista actual en localStorage y emite el nuevo arreglo por el BehaviorSubject para persistir la compra en el navegador y avisar al Navbar que actualice el contador.
  private guardarItemsEnStorage(items: ItemCarrito[]): void {
    localStorage.setItem(this.STORAGE_KEY_CARRITO, JSON.stringify(items));
    this.itemsSubject.next(items);
  }

  // Genera una cadena de texto única con los IDs y cantidades de los adicionales para identificar combinaciones idénticas de un plato y saber si se deben fusionar en una sola fila.
  private firmaAdicionales(adicionales: AdicionalCarrito[]): string {
    if (!adicionales || adicionales.length === 0) return 'none';
    return [...adicionales]
      .sort((a, b) => a.id_adicional - b.id_adicional)
      .map(a => `${a.id_adicional}x${a.cantidad}`)
      .join('|');
  }

  // Entrega la lista actual de platos en el carrito de forma síncrona para consultarla rápidamente sin necesidad de suscribirse.
  getItems(): ItemCarrito[] {
    return this.itemsSubject.value;
  }

  // Agrega un plato con sus adicionales al carrito para sumar la cantidad si ya existía con la misma personalización o crear una nueva entrada si es diferente.
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

  // Suma o resta la cantidad de un ítem para permitir ajustar el pedido desde los botones + y - y eliminar el ítem si la cantidad llega a 0.
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

  // Remueve un ítem específico por su índice para permitir al usuario descartar un plato del carrito y actualizar el total de inmediato.
  eliminarItem(index: number): void {
    const items = [...this.itemsSubject.value];
    if (index < 0 || index >= items.length) return;
    items.splice(index, 1);
    this.guardarItemsEnStorage(items);
  }

  // Borra todos los ítems de localStorage y emite un arreglo vacío por el BehaviorSubject para limpiar el carrito tras confirmar un pedido o al cerrar sesión.
  vaciarCarrito(): void {
    localStorage.removeItem(this.STORAGE_KEY_CARRITO);
    this.itemsSubject.next([]);
  }

  // Suma el precio del plato más la multiplicación de precio y cantidad de cada adicional para obtener el valor unitario exacto de cada línea del carrito.
  calcularPrecioUnitarioItem(item: ItemCarrito): number {
    const base = item.producto.precio || 0;
    const extras = (item.adicionales || []).reduce(
      (sum, a) => sum + (a.precio || 0) * (a.cantidad || 1),
      0
    );
    return base + extras;
  }

  // Acumula la cantidad de todos los platos para mostrar el número total de unidades en la burbuja roja del icono del carrito.
  getTotalCantidad(): number {
    return this.itemsSubject.value.reduce((acc, item) => acc + item.cantidad, 0);
  }

  // Multiplica el precio unitario completo por la cantidad de cada plato para calcular el monto total a pagar por el pedido.
  getTotalPrecio(): number {
    return this.itemsSubject.value.reduce((acc, item) => {
      return acc + this.calcularPrecioUnitarioItem(item) * item.cantidad;
    }, 0);
  }

  // --- Visibilidad del drawer del carrito ---

  // Emite true a través del BehaviorSubject para abrir el panel lateral del carrito cuando el usuario hace clic en el icono o añade comida.
  abrirCarrito(): void {
    this.abiertoSubject.next(true);
  }

  // Emite false a través del BehaviorSubject para cerrar el panel lateral del carrito cuando el usuario da clic en la X o en el fondo oscuro.
  cerrarCarrito(): void {
    this.abiertoSubject.next(false);
  }

  // Invierte el valor actual de apertura para alternar entre mostrar u ocultar el panel lateral con un solo botón.
  toggleCarrito(): void {
    this.abiertoSubject.next(!this.abiertoSubject.value);
  }

  // Emite una notificación sin datos a través del Subject para avisar que se creó un pedido con éxito y disparar la actualización de la tabla del perfil.
  notificarPedidoCreado(): void {
    this.pedidoCreadoSubject.next();
  }

  // Devuelve la lista de carritos en memoria para brindar compatibilidad con controladores o componentes antiguos que consultaban esta colección.
  getCarritos(): Carrito[] {
    return this.carritos;
  }
}
