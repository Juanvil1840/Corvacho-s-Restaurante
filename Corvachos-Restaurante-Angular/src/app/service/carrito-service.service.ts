import { Injectable } from '@angular/core';
import { Carrito } from '../models/carrito.model';
import { ClienteServiceService } from './cliente-service.service';

@Injectable({
  providedIn: 'root'
})
export class CarritoServiceService {

  private carritos: Carrito[] = [];

  constructor(
    private clienteService: ClienteServiceService
  ) {

    const clientes = this.clienteService.getClientes();

    this.carritos = [
      {
        id_carrito: 1,
        cliente: clientes[0]
      },
      {
        id_carrito: 2,
        cliente: clientes[1]
      },
      {
        id_carrito: 3,
        cliente: clientes[2]
      },
      {
        id_carrito: 4,
        cliente: clientes[3]
      },
      {
        id_carrito: 5,
        cliente: clientes[4]
      }
    ];
  }

  getCarritos(): Carrito[] {
    return this.carritos;
  }
}