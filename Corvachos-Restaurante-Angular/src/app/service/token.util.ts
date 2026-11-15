// Funciones para leer el JWT guardado en localStorage (sin inyección de dependencias,
// así el interceptor las puede usar sin crear dependencias circulares con los servicios)

export const TOKEN_KEY = 'token';

export interface PayloadJwt {
  sub?: string;
  rol?: string;
  exp?: number;
}

// El JWT tiene 3 partes separadas por "."; la del medio (payload) es JSON en Base64URL
export function leerPayload(token: string): PayloadJwt | null {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
  } catch {
    return null;
  }
}

// true si el token ya venció (exp viene en segundos)
export function tokenVencido(token: string): boolean {
  const payload = leerPayload(token);
  if (!payload?.exp) {
    return true;
  }
  return payload.exp * 1000 <= Date.now();
}

// Devuelve el token guardado si existe y no ha vencido
export function obtenerTokenValido(): string | null {
  const token = localStorage.getItem(TOKEN_KEY);
  if (!token || tokenVencido(token)) {
    return null;
  }
  return token;
}
