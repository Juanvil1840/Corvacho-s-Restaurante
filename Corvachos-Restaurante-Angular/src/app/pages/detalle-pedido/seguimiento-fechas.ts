// El backend envía fechas sin zona horaria ("2026-11-22T20:15:30.123456") en la hora del restaurante.
// Se recortan los microsegundos para que todos los navegadores las lean como hora local.
export function leerFecha(texto: string | null | undefined): Date | null {
  if (!texto) {
    return null;
  }
  const fecha = new Date(texto.replace(/(\.\d{3})\d+/, '$1'));
  return isNaN(fecha.getTime()) ? null : fecha;
}

// Hora corta, por ejemplo "8:45 p. m."
export function horaCorta(fecha: Date): string {
  return fecha.toLocaleTimeString('es-CO', { hour: 'numeric', minute: '2-digit' });
}
