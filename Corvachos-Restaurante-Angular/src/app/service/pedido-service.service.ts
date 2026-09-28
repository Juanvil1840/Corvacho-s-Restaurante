import { Injectable } from '@angular/core';
import { Pedido } from '../models/pedido.model';
import { ClienteServiceService } from './cliente-service.service';
import { OperadorServiceService } from './operador-service.service';
import { DomiciliarioServiceService } from './domiciliario-service.service';

@Injectable({
  providedIn: 'root'
})
export class PedidoServiceService {

  private pedidos: Pedido[] = [];

  constructor(
    private clienteService: ClienteServiceService,
    private operadorService: OperadorServiceService,
    private domiciliarioService: DomiciliarioServiceService
  ) {

    const clientes = this.clienteService.getClientes();
    const operadores = this.operadorService.getOperadores();
    const domiciliarios = this.domiciliarioService.getDomiciliarios();

    this.pedidos = [
      {
        id_Pedido: 1,
        estado: "Entregado",
        fechaCreacion: new Date(Date.now() - 30 * 60 * 1000),
        fechaEntrega: new Date(),
        cliente: clientes[0],
        operador: operadores[0],
        domiciliario: domiciliarios[0]
      },
      {
        id_Pedido: 2,
        estado: "Cocinando",
        fechaCreacion: new Date(Date.now() - 50 * 60 * 1000),
        fechaEntrega: new Date(),
        cliente: clientes[1],
        operador: operadores[1],
        domiciliario: domiciliarios[1]
      },
      {
        id_Pedido: 3,
        estado: "Enviado",
        fechaCreacion: new Date(Date.now() - 20 * 60 * 1000),
        fechaEntrega: new Date(),
        cliente: clientes[2],
        operador: operadores[2],
        domiciliario: domiciliarios[2]
      },
      {
        id_Pedido: 4,
        estado: "Recibido",
        fechaCreacion: new Date(Date.now() - 40 * 60 * 1000),
        fechaEntrega: new Date(),
        cliente: clientes[3],
        operador: operadores[3],
        domiciliario: domiciliarios[3]
      },
      {
        id_Pedido: 5,
        estado: "Recibido",
        fechaCreacion: new Date(Date.now() - 50 * 60 * 1000),
        fechaEntrega: new Date(),
        cliente: clientes[4],
        operador: operadores[4],
        domiciliario: domiciliarios[4]
      },
      {
        id_Pedido: 6,
        estado: "Cocinando",
        fechaCreacion: new Date(Date.now() - 50 * 60 * 1000),
        fechaEntrega: new Date(),
        cliente: clientes[2],
        operador: operadores[4],
        domiciliario: domiciliarios[4]
      }
    ];
  }

  getPedidos(): Pedido[] {
    return this.pedidos;
  }
}