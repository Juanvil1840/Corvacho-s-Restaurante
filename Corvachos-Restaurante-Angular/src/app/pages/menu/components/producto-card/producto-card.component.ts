import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Producto } from '../../../../models/producto.model';
import { CarritoService } from '../../../../service/carrito.service';

// Componente de tarjeta para mostrar la información resumida de un producto en el menú
@Component({
  selector: 'app-producto-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './producto-card.component.html',
  styleUrl: './producto-card.component.scss'
})
export class ProductoCardComponent {

  // Producto a mostrar y bandera temporal de confirmación al agregar al carrito
  @Input() producto!: Producto;
  agregado = false;

  constructor(private carritoService: CarritoService) {}

  // Agrega una unidad del producto sin adicionales directamente al carrito
  agregarRapido(): void {
    this.carritoService.agregarItem(this.producto, 1, null);
    this.agregado = true;
    // Restablece el estado visual después de unos instantes
    setTimeout(() => {
      this.agregado = false;
    }, 1800);
  }

}
