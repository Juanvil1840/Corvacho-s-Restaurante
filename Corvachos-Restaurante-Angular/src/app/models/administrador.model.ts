// Modelo que representa a un usuario administrador del sistema
export interface Administrador {
    id_administrador: number;
    nombre: string;
    usuario: string;
    contrasena?: string;
}
