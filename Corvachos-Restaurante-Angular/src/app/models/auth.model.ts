// Roles que maneja el backend (Spring Security)
export type Rol = 'ADMIN' | 'OPERADOR' | 'CLIENTE';

// Respuesta de los endpoints de login: token JWT, rol y datos del usuario
export interface AuthResponse<T> {
  token: string;
  rol: Rol;
  usuario: T;
}
