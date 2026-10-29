import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CategoriaSeccionComponent } from './components/categoria-seccion/categoria-seccion.component';
import { ProductoService } from '../../service/producto.service';
import { Producto } from '../../models/producto.model';

// Componente principal que muestra el menú del restaurante agrupado por categorías
@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, CategoriaSeccionComponent],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit {

  // Diccionario de productos agrupados por nombre de categoría y estados de la vista
  productosPorCategoria: { [categoria: string]: Producto[] } = {};
  cargando = true;
  error = '';

  constructor(private productoService: ProductoService) {}

  // Carga los productos disponibles al iniciar y los organiza por categoría
  ngOnInit(): void {
    this.productoService.getProductosDisponibles().subscribe({
      next: (productos) => {
        this.productosPorCategoria = {};

        // Agrupa cada producto dentro de su categoría correspondiente
        productos.forEach((p) => {
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

  // Retorna la lista de nombres de las categorías con productos disponibles
  getCategorias(): string[] {
    return Object.keys(this.productosPorCategoria);
  }
}
