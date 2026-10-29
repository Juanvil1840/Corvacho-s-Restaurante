import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Producto } from '../../../../models/producto.model';
import { Adicional } from '../../../../models/adicional.model';
import { AdicionalCarrito, CarritoService } from '../../../../service/carrito.service';

@Component({
  selector: 'app-detalle-info',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './detalle-info.component.html',
  styleUrl: './detalle-info.component.scss'
})
export class DetalleInfoComponent {

  @Input() producto!: Producto;
  @Input() adicionales: Adicional[] = [];

  cantidad = 1;
  adicionalesElegidos: Map<number, number> = new Map<number, number>();
  mensajeAgregado = false;

  constructor(private carritoService: CarritoService) {}

  cambiarCantidad(delta: number): void {
    const nueva = this.cantidad + delta;
    if (nueva >= 1 && nueva <= 99) {
      this.cantidad = nueva;
    }
  }

  limpiarAdicionales(): void {
    this.adicionalesElegidos.clear();
  }

  estaSeleccionado(idAdicional: number): boolean {
    return (this.adicionalesElegidos.get(idAdicional) || 0) > 0;
  }

  obtenerCantidadAdicional(idAdicional: number): number {
    return this.adicionalesElegidos.get(idAdicional) || 0;
  }

  toggleAdicional(adicional: Adicional): void {
    const id = adicional.id_adicional;
    if (this.estaSeleccionado(id)) {
      this.adicionalesElegidos.delete(id);
    } else {
      this.adicionalesElegidos.set(id, 1);
    }
  }

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

  get sinAdicionales(): boolean {
    return this.adicionalesElegidos.size === 0;
  }

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

  get precioTotalSeleccion(): number {
    const base = this.producto?.precio || 0;
    const extra = this.listaAdicionalesSeleccionados.reduce(
      (sum, a) => sum + a.precio * a.cantidad,
      0
    );
    return (base + extra) * this.cantidad;
  }

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

  abrirCarrito(): void {
    this.carritoService.abrirCarrito();
  }

}
