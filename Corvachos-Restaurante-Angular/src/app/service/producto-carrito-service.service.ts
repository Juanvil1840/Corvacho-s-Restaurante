import { Injectable } from '@angular/core';
import { ProductoCarrito } from '../models/productoCarrito.model';

@Injectable({
  providedIn: 'root'
})
export class ProductoCarritoServiceService {

  private productosCarritos: ProductoCarrito[] = [];

  constructor() {}

  getProductosCarritos(): ProductoCarrito[] {
    return this.productosCarritos;
  }
}
