// Estructura de un adicional en el historial: nombre y cantidad
export interface AdicionalHistorial {
  nombre: string;
  cantidad: number;
}

// Estructura de un detalle en el historial: cantidad, producto, adicionales y subtotal
export interface DetalleHistorial {
  cantidad: number;
  producto: string;
  adicionales: AdicionalHistorial[];
  subtotal: number;
}

// Estructura de un pedido en el historial del cliente
export interface PedidoHistorial {
  id_Pedido: number;
  estado: string;
  fechaCreacion: string;
  total: number;
  productos: DetalleHistorial[];
}