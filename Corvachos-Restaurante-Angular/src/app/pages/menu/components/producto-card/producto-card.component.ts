import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Producto } from '../../../../models/producto.model';
import { CarritoServiceService } from '../../../../service/carrito-service.service';

@Component({
  selector: 'app-producto-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './producto-card.component.html',
  styleUrl: './producto-card.component.scss'
})
export class ProductoCardComponent {

  @Input() producto!: Producto;
  agregado = false;

  constructor(private carritoService: CarritoServiceService) {}

  agregarRapido(): void {
    this.carritoService.agregarItem(this.producto, 1, null);
    this.agregado = true;
    setTimeout(() => {
      this.agregado = false;
    }, 1800);
  }

}
