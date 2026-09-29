import { Cliente } from "./cliente.model";
import { Domiciliario } from "./domiciliario.model";
import { Operador } from "./operador.model";

export interface Pedido {
    id_Pedido: number;
    estado: string;
    fechaCreacion: Date;
    fechaEntrega: Date;
    cliente: Cliente;
    operador: Operador;
    domiciliario: Domiciliario;
}