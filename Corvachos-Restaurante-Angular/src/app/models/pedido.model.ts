import { DetallePedido } from "./detallePedido.model";

// Datos del cliente que trae el detalle de un pedido
export interface ClientePedido {
    clienteId: number;
    nombre: string;
    apellido: string;
    telefono: string;
    direccion: string;
}

export interface OperadorPedido {
    id_operador: number;
    nombre: string;
}

export interface DomiciliarioPedido {
    id_domiciliario: number;
    nombre: string;
    celular: string;
}

export interface Pedido {
    id_Pedido: number;
    estado: string;
    fechaCreacion: string;
    fechaEntrega: string | null;
    cliente: ClientePedido;
    operador: OperadorPedido | null;
    domiciliario: DomiciliarioPedido | null;
    detallePedidos: DetallePedido[];
}

// Fila de la tabla de pedidos
export interface PedidoLista {
    id_Pedido: number;
    estado: string;
    fechaCreacion: string;
    clienteNombre: string | null;
    operadorNombre: string | null;
    domiciliarioNombre: string | null;
}

// Lo que se envia al backend para crear o actualizar un pedido (solo ids)
export interface PedidoGuardar {
    estado?: string;
    fechaCreacion?: string;
    fechaEntrega?: string | null;
    cliente: { clienteId: number };
    operador?: { id_operador: number } | null;
    domiciliario?: { id_domiciliario: number } | null;
    detallePedidos: {
        cantidad: number;
        producto: { id: number };
        adicionales: { id_adicional: number; cantidad: number }[];
    }[];
}
