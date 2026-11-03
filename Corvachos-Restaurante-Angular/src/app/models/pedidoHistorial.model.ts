// Pedido en el historial del cliente (coincide con PedidoHistorialDTO del backend)
export interface AdicionalHistorial {
  nombre: string;
  cantidad: number;
}

// Detalle de un producto dentro del historial de pedidos del cliente
export interface DetalleHistorial {
  cantidad: number;
  producto: string;
  adicionales: AdicionalHistorial[];
  subtotal: number;
}

// Resumen de un pedido mostrado en el historial del perfil del cliente
export interface PedidoHistorial {
  id_Pedido: number;
  estado: string;
  fechaCreacion: string;
  total: number;
  productos: DetalleHistorial[];
}
