// Pedido en el historial del cliente (coincide con PedidoHistorialDTO del backend)
export interface AdicionalHistorial {
  nombre: string;
  cantidad: number;
}

export interface DetalleHistorial {
  cantidad: number;
  producto: string;
  adicionales: AdicionalHistorial[];
  subtotal: number;
}

export interface PedidoHistorial {
  id_Pedido: number;
  estado: string;
  fechaCreacion: string;
  total: number;
  productos: DetalleHistorial[];
}
