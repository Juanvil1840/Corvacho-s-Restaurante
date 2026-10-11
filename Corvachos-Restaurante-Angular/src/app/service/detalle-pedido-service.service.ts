import { Injectable } from '@angular/core';
import { DetallePedido } from '../models/detallePedido.model';
import { ProductoServiceService } from './producto-service.service';
import { PedidoServiceService } from './pedido-service.service';
import { AdicionalServiceService } from './adicional-service.service';

@Injectable({
  providedIn: 'root'
})
export class DetallePedidoServiceService {

  private detallesPedidos: DetallePedido[] = [];

  constructor(
    private productoService: ProductoServiceService,
    private pedidoService: PedidoServiceService,
    private adicionalService: AdicionalServiceService
  ) {

    const pedidos = this.pedidoService.getPedidos();
    const adicionales = this.adicionalService.getAdicionales();

    this.productoService.getProductos().subscribe({
      next: (productos) => {

        if (productos.length === 0) {
          this.detallesPedidos = [];
          return;
        }

        this.detallesPedidos = [
          {
            id_DetallePedido: 1,
            cantidad: 2,
            producto: productos[0],
            pedido: pedidos[0],
            adicional: adicionales[0]
          },
          {
            id_DetallePedido: 2,
            cantidad: 1,
            producto: productos[1 % productos.length],
            pedido: pedidos[1],
            adicional: adicionales[1]
          },
          {
            id_DetallePedido: 3,
            cantidad: 3,
            producto: productos[2 % productos.length],
            pedido: pedidos[2],
            adicional: adicionales[2]
          },
          {
            id_DetallePedido: 4,
            cantidad: 2,
            producto: productos[3 % productos.length],
            pedido: pedidos[3],
            adicional: adicionales[3]
          },
          {
            id_DetallePedido: 5,
            cantidad: 1,
            producto: productos[4 % productos.length],
            pedido: pedidos[4],
            adicional: adicionales[4]
          },
          {
            id_DetallePedido: 6,
            cantidad: 1,
            producto: productos[4 % productos.length],
            pedido: pedidos[5],
            adicional: adicionales[0]
          }
        ];
      },
      error: (error) => {
        console.error('Error al cargar productos para los detalles del pedido:', error);
        this.detallesPedidos = [];
      }
    });
  }

  getDetallesPedidos(): DetallePedido[] {
    return this.detallesPedidos;
  }
}