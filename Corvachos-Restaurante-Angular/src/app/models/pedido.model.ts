import { DetallePedido } from "./detallePedido.model";

// Estructura del cliente dentro de un pedido: id, nombre, apellido, telefono y direccion
export interface ClientePedido {
    clienteId: number;
    nombre: string;
    apellido: string;
    telefono: string;
    direccion: string;
}

// Estructura del operador dentro de un pedido: id y nombre
export interface OperadorPedido {
    id_operador: number;
    nombre: string;
}

// Estructura del domiciliario dentro de un pedido: id, nombre y celular
export interface DomiciliarioPedido {
    id_domiciliario: number;
    nombre: string;
    celular: string;
}

// Estructura completa de un pedido con sus datos y detalles
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

// Estructura de una fila de la tabla de pedidos
export interface PedidoLista {
    id_Pedido: number;
    estado: string;
    fechaCreacion: string;
    clienteNombre: string | null;
    operadorNombre: string | null;
    domiciliarioNombre: string | null;
}

// Estructura de los datos para crear o actualizar un pedido
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