import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductoServiceService } from '../../service/producto-service.service';
import { Producto } from '../../models/producto.model';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, RouterLink,FooterComponent],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit {

  productosPorCategoria: { [categoria: string]: Producto[] } = {};

  constructor(private productoService: ProductoServiceService) {}

  ngOnInit(): void {
    const productos = this.productoService.getProductos();
    // Agrupar productos por categoria
    productos.forEach(p => {
      const cat = p.categoria?.nombre || 'Sin categoria';
      if (!this.productosPorCategoria[cat]) {
        this.productosPorCategoria[cat] = [];
      }
      this.productosPorCategoria[cat].push(p);
    });
  }

  // Para iterar en el HTML (porque *ngFor no soporta objetos directamente)
  getCategorias(): string[] {
    return Object.keys(this.productosPorCategoria);
  }
}