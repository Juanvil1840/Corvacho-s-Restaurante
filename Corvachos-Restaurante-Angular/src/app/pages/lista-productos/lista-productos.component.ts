import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductoServiceService } from '../../service/producto-service.service';
import { Producto } from '../../models/producto.model';
import { ProductoTablaComponent } from './components/producto-tabla/producto-tabla.component';

@Component({
  selector: 'app-lista-productos',
  standalone: true,
  imports: [CommonModule, RouterLink, ProductoTablaComponent],
  templateUrl: './lista-productos.component.html',
  styleUrl: './lista-productos.component.scss'
})
export class ListaProductosComponent implements OnInit {

  productos: Producto[] = [];
  cargando = false;
  error = '';

  constructor(private productoService: ProductoServiceService) {}

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.cargando = true;
    this.error = '';

    this.productoService.getProductos().subscribe({
      next: (productos) => {
        this.productos = productos;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar los productos:', error);
        this.error = 'No fue posible cargar los productos. Intenta nuevamente.';
        this.cargando = false;
      }
    });
  }

  eliminarProducto(id: number): void {
    if (confirm('¿Eliminar este producto?')) {
      this.productoService.eliminarProducto(id).subscribe({
        next: () => {
          this.cargarProductos();
        },
        error: (error) => {
          console.error('Error al eliminar el producto:', error);
          if (error.status === 409 && error.error?.mensaje) {
            this.error = error.error.mensaje;
          } else {
            this.error = 'No fue posible eliminar el producto.';
          }
        }
      });
    }
  }
}
