// Tipos de descuento de un cupon
export type TipoCupon = 'PORCENTAJE' | 'VALOR_FIJO';

// Cupon como lo ve el administrador (coincide con CuponDTO del backend)
export interface Cupon {
  id_cupon: number;
  codigo: string;
  descripcion: string | null;
  tipo: TipoCupon;
  valor: number;
  compraMinima: number;
  usosMaximos: number | null;
  usos: number;
  fechaVencimiento: string | null;
  activo: boolean;
  estado?: 'Activo' | 'Inactivo' | 'Vencido' | 'Agotado';
}

// Resultado de probar un cupon en el carrito (coincide con CuponValidadoDTO)
export interface CuponValidado {
  codigo: string;
  descripcion: string | null;
  tipo: TipoCupon;
  valor: number;
  subtotal: number;
  descuento: number;
  total: number;
}
