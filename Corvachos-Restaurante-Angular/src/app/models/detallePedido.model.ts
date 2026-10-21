import { Adicional } from "./adicional.model";
import { Pedido } from "./pedido.model";
import { Producto } from "./producto.model";

export interface AdicionalSeleccionadoItem {
    id_adicional: number;
    nombre: string;
    precio: number;
    cantidad: number;
}

export interface DetallePedido {
    id_DetallePedido: number;
    cantidad: number;
    producto: Producto;
    pedido: Pedido;
    adicional: Adicional;
    adicionales?: AdicionalSeleccionadoItem[];
}
