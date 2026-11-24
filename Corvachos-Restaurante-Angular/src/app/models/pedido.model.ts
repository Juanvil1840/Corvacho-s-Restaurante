import { DetallePedido } from "./detallePedido.model";

// Datos del cliente que trae el detalle de un pedido
export interface ClientePedido {
    clienteId: number;
    nombre: string;
    apellido: string;
    telefono: string;
    direccion: string;
}

// Datos resumidos del operador asignado al pedido
export interface OperadorPedido {
    id_operador: number;
    nombre: string;
}

// Datos resumidos del domiciliario asignado al pedido
export interface DomiciliarioPedido {
    id_domiciliario: number;
    nombre: string;
    celular: string;
}

// Modelo completo de un pedido con su cliente, operador, domiciliario y detalles
export interface Pedido {
    id_Pedido: number;
    estado: string;
    fechaCreacion: string;
    fechaEntrega: string | null;
    cliente: ClientePedido;
    operador: OperadorPedido | null;
    domiciliario: DomiciliarioPedido | null;
    detallePedidos: DetallePedido[];
    // Código del cupón usado (null si no usó) y valor descontado
    cupon?: string | null;
    cuponTipo?: string | null;
    cuponValor?: number | null;
    descuento?: number;
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
    // Cupón que escribió el cliente; el backend calcula el descuento
    codigoCupon?: string | null;
    detallePedidos: {
        cantidad: number;
        producto: { id: number };
        adicionales: { id_adicional: number; cantidad: number }[];
    }[];
}
