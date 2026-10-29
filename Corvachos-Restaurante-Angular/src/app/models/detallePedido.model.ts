// Estructura de un adicional dentro de un pedido: id, nombre, precio y cantidad
export interface AdicionalPedido {
    id_adicional: number;
    nombre: string;
    precio: number;
    cantidad: number;
}

// Estructura de un producto dentro de un pedido: id, nombre, precio, descripcion e imagen
export interface ProductoPedido {
    id: number;
    nombre: string;
    precio: number;
    descripcion: string;
    imagen: string;
}

// Estructura de un detalle de pedido: id, cantidad, producto y adicionales
export interface DetallePedido {
    id_DetallePedido: number;
    cantidad: number;
    producto: ProductoPedido;
    adicionales: AdicionalPedido[];
}