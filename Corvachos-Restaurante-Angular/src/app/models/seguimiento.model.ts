// Seguimiento en vivo de un pedido (coincide con SeguimientoPedidoDTO del backend)
export interface SeguimientoPedido {
  pedidoId: number;
  estado: string;
  // 1 = Recibido, 2 = Cocinando, 3 = Enviado, 4 = Entregado
  paso: number;
  fechaCreacion: string;
  fechaEntrega: string | null;
  // Hora aproximada de llegada (null cuando ya se entregó)
  llegadaEstimada: string | null;
  minutosPromedioEntrega: number;
  domiciliario: string | null;
  celularDomiciliario: string | null;
}

// Estado del canal en vivo con el servidor
export type EstadoConexion = 'conectando' | 'en-vivo' | 'reconectando';

// Lo que emite el servicio: un nuevo estado del pedido o un cambio en la conexión
export type EventoSeguimiento =
  | { tipo: 'estado'; datos: SeguimientoPedido }
  | { tipo: 'conexion'; estado: EstadoConexion };
