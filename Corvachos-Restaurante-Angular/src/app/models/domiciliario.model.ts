import { Operador } from "./operador.model";

// Modelo que representa a un domiciliario encargado de entregar los pedidos
export interface Domiciliario {
    id_domiciliario: number;
    nombre: string;
    celular: string;
    cedula: string;
    informaciondisp: boolean;
    activo?: boolean;
    operador: Operador | null;
}
