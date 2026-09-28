import { Injectable } from '@angular/core';
import { ProductoCarrito } from '../models/productoCarrito.model';
import { ProductoServiceService } from './producto-service.service';
import { CarritoServiceService } from './carrito-service.service';

@Injectable({
  providedIn: 'root'
})
export class ProductoCarritoServiceService {

  private productosCarritos: ProductoCarrito[] = [];

  constructor(
    private productoService: ProductoServiceService,
    private carritoService: CarritoServiceService
  ) {

    const productos = this.productoService.getProductos();
    const carritos = this.carritoService.getCarritos();

    this.productosCarritos = [
      {
        id_ProductoCarrito: 1,
        cantidadP: 2,
        producto: productos[0],
        carrito: carritos[0]
      },
      {
        id_ProductoCarrito: 2,
        cantidadP: 1,
        producto: productos[1],
        carrito: carritos[1]
      },
      {
        id_ProductoCarrito: 3,
        cantidadP: 3,
        producto: productos[2],
        carrito: carritos[2]
      },
      {
        id_ProductoCarrito: 4,
        cantidadP: 2,
        producto: productos[3],
        carrito: carritos[3]
      },
      {
        id_ProductoCarrito: 5,
        cantidadP: 1,
        producto: productos[4],
        carrito: carritos[4]
      },
      {
        id_ProductoCarrito: 6,
        cantidadP: 4,
        producto: productos[5],
        carrito: carritos[0]
      }
    ];
  }

  getProductosCarritos(): ProductoCarrito[] {
    return this.productosCarritos;
  }
}