// Utilidades de formato y fechas compartidas por el dashboard de reportes

const MONEDA = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 });
const MONEDA_COMPACTA = new Intl.NumberFormat('es-CO', {
  style: 'currency', currency: 'COP', notation: 'compact', maximumFractionDigits: 1
});
const ENTERO = new Intl.NumberFormat('es-CO', { maximumFractionDigits: 0 });
const DECIMAL = new Intl.NumberFormat('es-CO', { maximumFractionDigits: 1 });

export type TipoFormato = 'moneda' | 'compacta' | 'entero' | 'decimal';

// Formatea un numero segun el tipo pedido (pesos colombianos sin decimales)
export function formatear(valor: number, tipo: TipoFormato): string {
  switch (tipo) {
    case 'moneda': return MONEDA.format(valor);
    case 'compacta': return MONEDA_COMPACTA.format(valor);
    case 'decimal': return DECIMAL.format(valor);
    default: return ENTERO.format(valor);
  }
}

// Fecha local en formato yyyy-MM-dd (toISOString usa UTC y puede correr el dia)
export function fechaIso(fecha: Date): string {
  const mes = String(fecha.getMonth() + 1).padStart(2, '0');
  const dia = String(fecha.getDate()).padStart(2, '0');
  return `${fecha.getFullYear()}-${mes}-${dia}`;
}

// Convierte yyyy-MM-dd en Date local (new Date('yyyy-MM-dd') lo interpreta como UTC)
export function desdeIso(iso: string): Date {
  const [anio, mes, dia] = iso.split('-').map(Number);
  return new Date(anio, mes - 1, dia);
}

// Fecha corta legible, por ejemplo "21 nov 2026"
export function fechaLegible(iso: string): string {
  return desdeIso(iso).toLocaleDateString('es-CO', { day: 'numeric', month: 'short', year: 'numeric' });
}
