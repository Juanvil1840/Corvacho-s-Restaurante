// Adicional incluido dentro de un detalle de pedido con su cantidad elegida
export interface AdicionalPedido {
    id_adicional: number;
    nombre: string;
    precio: number;
    cantidad: number;
}

// Datos básicos del producto asociado a una línea del pedido
export interface ProductoPedido {
    id: number;
    nombre: string;
    precio: number;
    descripcion: string;
    imagen: string;
}

// Representa un ítem o línea de un pedido con su producto, cantidad y adicionales
export interface DetallePedido {
    id_DetallePedido: number;
    cantidad: number;
    producto: ProductoPedido;
    adicionales: AdicionalPedido[];
}
