export interface Resena {
  id: number;
  pedidoId: number;
  estadoPedido: string;
  clienteId: number;
  cliente: string;
  correoCliente: string;
  puntuacion: number;
  comentario: string | null;
  fechaCreacion: string;
}

export interface CrearResena {
  puntuacion: number;
  comentario?: string;
}
