import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CategoriaSeccionComponent } from './components/categoria-seccion/categoria-seccion.component';
import { ProductoServiceService } from '../../service/producto-service.service';
import { Producto } from '../../models/producto.model';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, CategoriaSeccionComponent],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit {

  productosPorCategoria: { [categoria: string]: Producto[] } = {};

  constructor(private productoService: ProductoServiceService) {}

  ngOnInit(): void {
    const productos = this.productoService.getProductos();
    productos.forEach(p => {
      const cat = p.categoria?.nombre || 'Sin categoria';
      if (!this.productosPorCategoria[cat]) {
        this.productosPorCategoria[cat] = [];
      }
      this.productosPorCategoria[cat].push(p);
    });
  }

  getCategorias(): string[] {
    return Object.keys(this.productosPorCategoria);
  }
}