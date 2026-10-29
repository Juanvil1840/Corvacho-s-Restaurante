import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Producto } from '../../../../models/producto.model';
import { Adicional } from '../../../../models/adicional.model';
import { AdicionalCarrito, CarritoService } from '../../../../service/carrito.service';

// Subcomponente que muestra los datos del producto y permite elegir cantidad y adicionales para agregarlo al carrito
@Component({
  selector: 'app-detalle-info',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './detalle-info.component.html',
  styleUrl: './detalle-info.component.scss'
})
export class DetalleInfoComponent {

  // Producto actual y lista de adicionales disponibles para su categoría
  @Input() producto!: Producto;
  @Input() adicionales: Adicional[] = [];

  // Cantidad de unidades del producto, mapa de adicionales elegidos y estado de confirmación
  cantidad = 1;
  adicionalesElegidos: Map<number, number> = new Map<number, number>();
  mensajeAgregado = false;

  constructor(private carritoService: CarritoService) {}

  // Aumenta o disminuye la cantidad del producto entre 1 y 99
  cambiarCantidad(delta: number): void {
    const nueva = this.cantidad + delta;
    if (nueva >= 1 && nueva <= 99) {
      this.cantidad = nueva;
    }
  }

  // Quita todos los adicionales seleccionados
  limpiarAdicionales(): void {
    this.adicionalesElegidos.clear();
  }

  // Verifica si un adicional específico está seleccionado con al menos 1 unidad
  estaSeleccionado(idAdicional: number): boolean {
    return (this.adicionalesElegidos.get(idAdicional) || 0) > 0;
  }

  // Obtiene la cantidad seleccionada de un adicional por su ID
  obtenerCantidadAdicional(idAdicional: number): number {
    return this.adicionalesElegidos.get(idAdicional) || 0;
  }

  // Marca o desmarca un adicional al hacer clic sobre su tarjeta
  toggleAdicional(adicional: Adicional): void {
    const id = adicional.id_adicional;
    if (this.estaSeleccionado(id)) {
      this.adicionalesElegidos.delete(id);
    } else {
      this.adicionalesElegidos.set(id, 1);
    }
  }

  // Modifica la cantidad de un adicional seleccionado o lo desmarca si baja a cero
  cambiarCantidadAdicional(adicional: Adicional, delta: number, event: Event): void {
    event.stopPropagation();
    const id = adicional.id_adicional;
    const actual = this.adicionalesElegidos.get(id) || 0;
    const nueva = actual + delta;
    if (nueva <= 0) {
      this.adicionalesElegidos.delete(id);
    } else if (nueva <= 99) {
      this.adicionalesElegidos.set(id, nueva);
    }
  }

  // Indica si el usuario no ha elegido ningún adicional
  get sinAdicionales(): boolean {
    return this.adicionalesElegidos.size === 0;
  }

  // Construye la lista de adicionales seleccionados con el formato requerido por el carrito
  get listaAdicionalesSeleccionados(): AdicionalCarrito[] {
    const resultado: AdicionalCarrito[] = [];
    for (const adic of this.adicionales) {
      const cant = this.adicionalesElegidos.get(adic.id_adicional) || 0;
      if (cant > 0) {
        resultado.push({
          id_adicional: adic.id_adicional,
          nombre: adic.nombre,
          precio: adic.precio || 0,
          cantidad: cant
        });
      }
    }
    return resultado;
  }

  // Calcula el precio total de la selección actual incluyendo adicionales y cantidad
  get precioTotalSeleccion(): number {
    const base = this.producto?.precio || 0;
    const extra = this.listaAdicionalesSeleccionados.reduce(
      (sum, a) => sum + a.precio * a.cantidad,
      0
    );
    return (base + extra) * this.cantidad;
  }

  // Envía el producto con la cantidad y adicionales elegidos al carrito y muestra confirmación temporal
  agregarAlCarrito(): void {
    if (!this.producto) return;
    this.carritoService.agregarItem(
      this.producto,
      this.cantidad,
      this.listaAdicionalesSeleccionados
    );
    this.mensajeAgregado = true;
    setTimeout(() => {
      this.mensajeAgregado = false;
    }, 3500);
  }

  // Abre el panel lateral del carrito
  abrirCarrito(): void {
    this.carritoService.abrirCarrito();
  }

}
