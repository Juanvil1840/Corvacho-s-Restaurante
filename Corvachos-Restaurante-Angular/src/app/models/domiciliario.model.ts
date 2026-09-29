import { Operador } from "./operador.model";

export interface Domiciliario {
    id_domiciliario: number;
    nombre: string;
    celular: string;
    cedula: string;
    informaciondisp: boolean;
    operador: Operador;
}