// Estructura de un operador: id, nombre, usuario y contrasena
export interface Operador {
    id_operador: number;
    nombre: string;
    usuario: string;
    contrasena?: string;
}