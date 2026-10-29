import { Categoria } from "./categoria.model";

// Modelo que representa un ingrediente o acompañamiento adicional asociado a una categoría
export interface Adicional {
    id_adicional: number;
    nombre: string
    precio: number;
    disponible: boolean;
    categoria: Categoria;
}
