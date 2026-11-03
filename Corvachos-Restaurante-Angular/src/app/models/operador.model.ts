// Modelo que representa a un usuario operador que gestiona pedidos y domiciliarios
export interface Operador {
    id_operador: number;
    nombre: string;
    usuario: string;
    contrasena?: string;
}
