import { Injectable } from '@angular/core';
import { CarritoService } from './carrito.service';
import { AuthResponse, Rol } from '../models/auth.model';
import { TOKEN_KEY, leerPayload, tokenVencido } from './token.util';

// Llaves que se usan en localStorage
const ROL_KEY = 'rol';
// Datos del usuario que ya usaban los componentes (se mantienen)
const DATOS_POR_ROL: Record<Rol, string> = {
  ADMIN: 'administrador',
  OPERADOR: 'operador',
  CLIENTE: 'cliente'
};

// Servicio que maneja la sesión con JWT: guardar el token, saber el rol, cerrar sesión
// y decidir a qué página va cada rol
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private carritoService: CarritoService) { }

  // Guarda el token y los datos del usuario después del login.
  // Solo puede haber una sesión a la vez, así que se borra la de cualquier otro rol
  iniciarSesion<T>(respuesta: AuthResponse<T>): void {
    this.limpiarSesion();
    localStorage.setItem(TOKEN_KEY, respuesta.token);
    localStorage.setItem(ROL_KEY, respuesta.rol);
    if (respuesta.rol === 'CLIENTE') {
      // El carrito guarda al cliente en localStorage y carga su carrito del backend
      this.carritoService.setClienteLogueado(respuesta.usuario as any);
    } else {
      localStorage.setItem(DATOS_POR_ROL[respuesta.rol], JSON.stringify(respuesta.usuario));
    }
  }

  // Cierra la sesión: borra el token, el rol y los datos del usuario
  cerrarSesion(): void {
    this.limpiarSesion();
  }

  getToken(): string | null {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
      return null;
    }
    // Si el token ya venció se cierra la sesión
    if (tokenVencido(token)) {
      this.limpiarSesion();
      return null;
    }
    return token;
  }

  // Hay sesión si existe un token que no ha vencido
  estaAutenticado(): boolean {
    return this.getToken() !== null;
  }

  // Rol del usuario, leído del token (claim "rol")
  getRol(): Rol | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }
    const payload = leerPayload(token);
    return (payload?.rol as Rol) ?? (localStorage.getItem(ROL_KEY) as Rol | null);
  }

  tieneRol(roles: Rol[]): boolean {
    const rol = this.getRol();
    return rol !== null && roles.includes(rol);
  }

  // Página de inicio de cada rol
  rutaInicio(rol: Rol | null = this.getRol()): string {
    switch (rol) {
      case 'ADMIN':
        return '/portal-admin';
      case 'OPERADOR':
        return '/portal-operario';
      case 'CLIENTE': {
        const cliente = this.carritoService.getClienteLogueado();
        return cliente?.clienteId ? `/clientes/perfil/${cliente.clienteId}` : '/menu';
      }
      default:
        return '/';
    }
  }

  // Página de login que corresponde a un rol
  rutaLogin(rol: Rol | undefined): string {
    if (rol === 'ADMIN') {
      return '/login-admin';
    }
    if (rol === 'OPERADOR') {
      return '/login-operario';
    }
    return '/login';
  }

  private limpiarSesion(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(ROL_KEY);
    localStorage.removeItem(DATOS_POR_ROL.ADMIN);
    localStorage.removeItem(DATOS_POR_ROL.OPERADOR);
    // Borra el cliente de localStorage y vacía el carrito en memoria (el guardado en el backend no se toca)
    this.carritoService.cerrarSesionCliente();
  }
}
