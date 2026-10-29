import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ProductoService } from '../../service/producto.service';
import { Producto } from '../../models/producto.model';
import { ProductoTablaComponent } from './components/producto-tabla/producto-tabla.component';

// Componente para listar, eliminar y cambiar la disponibilidad de los productos
@Component({
  selector: 'app-lista-productos',
  standalone: true,
  imports: [CommonModule, RouterLink, ProductoTablaComponent],
  templateUrl: './lista-productos.component.html',
  styleUrl: './lista-productos.component.scss'
})
export class ListaProductosComponent implements OnInit {

  // Lista de productos y estados de carga y error
  productos: Producto[] = [];
  cargando = false;
  error = '';

  constructor(
    private productoService: ProductoService,
    private router: Router
  ) {}

  // Carga el catálogo de productos al iniciar el componente
  ngOnInit(): void {
    this.cargarProductos();
  }

  // Obtiene todos los productos desde el servicio
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

  // Confirma y elimina un producto por su ID, o redirige a la página de error si está asociado a un pedido
  eliminarProducto(id: number): void {
    if (!confirm('¿Eliminar este producto?')) {
      return;
    }

    this.error = '';

    this.productoService.eliminarProducto(id).subscribe({
      next: () => {
        // Recarga los productos tras eliminar exitosamente
        this.cargarProductos();
      },
      error: (error) => {
        console.error('Error al eliminar el producto:', error);
        const mensajeError = error.error?.mensaje 
          || error.error?.message 
          || 'Excepción: No se puede eliminar el producto porque está anidado a un pedido.';
        
        // Redirige a la vista de error mostrando el motivo del fallo
        this.router.navigate(['/error'], {
          queryParams: { mensaje: mensajeError }
        });
      }
    });
  }

  // Alterna el estado de disponibilidad de un producto en el menú
  toggleDisponible(producto: Producto): void {
    const nuevoEstado = !producto.disponible;
    const productoActualizado: Producto = {
      ...producto,
      disponible: nuevoEstado
    };

    this.error = '';

    this.productoService.actualizarProducto(productoActualizado).subscribe({
      next: (actualizado) => {
        // Actualiza el estado local del producto con la respuesta del servidor
        producto.disponible = actualizado.disponible;
      },
      error: (error) => {
        console.error('Error al actualizar disponibilidad:', error);
        this.error = 'No fue posible actualizar la disponibilidad del producto.';
      }
    });
  }
}
