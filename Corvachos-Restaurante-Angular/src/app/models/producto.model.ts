import { Categoria } from "./categoria.model";

// Estructura de un producto: id, nombre, precio, descripcion, imagen, disponibilidad y categoria
export interface Producto {
    id: number;
    nombre: string;
    precio: number;
    descripcion: string;
    imagen: string;
    disponible: boolean;
    categoria: Categoria;
}