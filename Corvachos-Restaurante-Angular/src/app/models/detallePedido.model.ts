export interface AdicionalPedido {
    id_adicional: number;
    nombre: string;
    precio: number;
    cantidad: number;
}

export interface ProductoPedido {
    id: number;
    nombre: string;
    precio: number;
    descripcion: string;
    imagen: string;
}

export interface DetallePedido {
    id_DetallePedido: number;
    cantidad: number;
    producto: ProductoPedido;
    adicionales: AdicionalPedido[];
}
