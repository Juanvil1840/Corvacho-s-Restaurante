import { Cliente } from "./cliente.model";
import { Domiciliario } from "./domiciliario.model";
import { Operador } from "./operador.model";

export interface Pedido {
    id_Pedido: number;
    estado: string;
    fechaCreacion: Date | string;
    fechaEntrega?: Date | string | null;
    cliente: Cliente;
    operador?: Operador | null;
    domiciliario?: Domiciliario | null;
    detallePedidos?: any[];
}