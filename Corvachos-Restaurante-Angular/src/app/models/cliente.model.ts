// Modelo que representa la información de un cliente registrado en el restaurante
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
