import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductoServiceService } from '../../service/producto-service.service';
import { Producto } from '../../models/producto.model';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-lista-productos',
  standalone: true,
  imports: [CommonModule, RouterLink, FooterComponent],
  templateUrl: './lista-productos.component.html',
  styleUrl: './lista-productos.component.scss'
})
export class ListaProductosComponent implements OnInit {

  productos: Producto[] = [];

  constructor(private productoService: ProductoServiceService) {}

  ngOnInit(): void {
    this.productos = this.productoService.getProductos();
  }

  eliminarProducto(id: number): void {
  if (confirm('¿Eliminar este producto?')) {
    this.productoService.eliminarProducto(id);
    this.productos = this.productoService.getProductos();
  }
}
}