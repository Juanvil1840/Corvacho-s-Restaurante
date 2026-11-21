// Modelos del dashboard de reportes del administrador (reflejan ReporteDashboardDTO del backend)

export interface Periodo {
  desde: string;
  hasta: string;
  dias: number;
}

// Indicadores principales; las variaciones son porcentajes frente al periodo anterior (null si no hay base)
export interface Indicadores {
  ventas: number;
  variacionVentas: number | null;
  pedidos: number;
  variacionPedidos: number | null;
  ticketPromedio: number;
  variacionTicket: number | null;
  clientesUnicos: number;
  variacionClientes: number | null;
  unidadesVendidas: number;
  ingresosAdicionales: number;
  porcentajeAdicionales: number;
  minutosPromedioEntrega: number | null;
  pedidosActivos: number;
}

export interface VentaDia {
  fecha: string;
  ventas: number;
  pedidos: number;
}

// Fila de un ranking (platos, adicionales, categorias o clientes)
export interface Ranking {
  id: number | null;
  nombre: string;
  detalle: string | null;
  imagen: string | null;
  cantidad: number;
  total: number;
}

export interface Conteo {
  nombre: string;
  cantidad: number;
}

export interface ReporteDashboard {
  periodo: Periodo;
  indicadores: Indicadores;
  ventasPorDia: VentaDia[];
  topProductos: Ranking[];
  topAdicionales: Ranking[];
  ventasPorCategoria: Ranking[];
  topClientes: Ranking[];
  pedidosPorEstado: Conteo[];
  // Matriz 7x24: fila 0 = lunes ... fila 6 = domingo, columna = hora del dia
  mapaCalor: number[][];
}

// Rango de fechas en formato ISO (yyyy-MM-dd)
export interface RangoFechas {
  desde: string;
  hasta: string;
}
