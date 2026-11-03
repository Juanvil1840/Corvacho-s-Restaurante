import { Categoria } from "./categoria.model";

// Modelo que representa un plato o producto del menú del restaurante
export interface Producto{
    id: number;
    nombre: string;
    precio: number;
    descripcion: string;
    imagen: string;
    disponible: boolean;
    categoria: Categoria;
}
