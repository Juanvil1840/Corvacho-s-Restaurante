import { Operador } from "./operador.model";

// Estructura de un domiciliario: id, nombre, celular, cedula, disponibilidad, estado y operador
export interface Domiciliario {
    id_domiciliario: number;
    nombre: string;
    celular: string;
    cedula: string;
    informaciondisp: boolean;
    activo?: boolean;
    operador: Operador | null;
}