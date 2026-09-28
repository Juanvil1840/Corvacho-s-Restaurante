import { Categoria } from "./categoria.model";

export interface Adicional {
    id_adicional: number;
    nombre: string
    precio: number;
    disponible: boolean;
    categoria: Categoria;
}