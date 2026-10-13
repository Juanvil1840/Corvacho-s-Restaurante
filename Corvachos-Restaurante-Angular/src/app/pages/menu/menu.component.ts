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
  cargando = true;
  error = '';

  constructor(private productoService: ProductoServiceService) {}

  ngOnInit(): void {
    this.productoService.getProductos().subscribe({
      next: (productos) => {
        this.productosPorCategoria = {};

        productos.filter((p) => p.disponible).forEach((p) => {
          const cat = p.categoria?.nombre || 'Sin categoria';

          if (!this.productosPorCategoria[cat]) {
            this.productosPorCategoria[cat] = [];
          }

          this.productosPorCategoria[cat].push(p);
        });

        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar el menú:', err);
        this.error = 'No fue posible cargar el menú. Intenta nuevamente.';
        this.cargando = false;
      }
    });
  }

  getCategorias(): string[] {
    return Object.keys(this.productosPorCategoria);
  }
}