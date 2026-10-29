import { Categoria } from "./categoria.model";

// Estructura de un adicional: id, nombre, precio, disponible y categoria
export interface Adicional {
    id_adicional: number;
    nombre: string;
    precio: number;
    disponible: boolean;
    categoria: Categoria;
}