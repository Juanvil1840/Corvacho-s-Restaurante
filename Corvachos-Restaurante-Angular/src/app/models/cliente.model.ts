// Estructura de un cliente: id, nombre, apellido, correo, contrasena, telefono, direccion y estado
export interface Cliente {
    clienteId: number;
    nombre: string;
    apellido: string;
    correo: string;
    contrasena?: string;
    telefono: string;
    direccion: string;
    activo: boolean;
}