// Estructura de un administrador: id, nombre, usuario y contraseña
export interface Administrador {
    id_administrador: number;
    nombre: string;
    usuario: string;
    contrasena?: string;
}