import { Injectable } from '@angular/core';
import { Domiciliario } from '../models/domiciliario.model';

@Injectable({
  providedIn: 'root'
})
export class DomiciliarioServiceService {

  private domiciliarios: Domiciliario[] = [
    {
      id_domiciliario: 1,
      nombre: "Juls Caicedo",
      celular: "3056677848",
      cedula: "0000238282",
      informaciondisp: true,
      operador: {
        id_operador: 1,
        nombre: "Kika Nieto",
        usuario: "KikaNieto_",
        contrasena: "1234"
      }
    },
    {
      id_domiciliario: 2,
      nombre: "Juancho rodriguez",
      celular: "3234560090",
      cedula: "0000129393",
      informaciondisp: true,
      operador: {
        id_operador: 2,
        nombre: "Nicolas Almendra",
        usuario: "NicoAla_08",
        contrasena: "4322"
      }
    },
    {
      id_domiciliario: 3,
      nombre: "Esteban Bogotá",
      celular: "3223388848",
      cedula: "0000443382",
      informaciondisp: true,
      operador: {
        id_operador: 3,
        nombre: "Mafe Cruz",
        usuario: "Mafecruz_0909",
        contrasena: "0987"
      }
    },
    {
      id_domiciliario: 4,
      nombre: "Pablo Rincón",
      celular: "3056677859",
      cedula: "0000255722",
      informaciondisp: true,
      operador: {
        id_operador: 4,
        nombre: "Samuel Corredor",
        usuario: "SamCor__",
        contrasena: "6392"
      }
    },
      {
      id_domiciliario: 5,
      nombre: "Julian España",
      celular: "3056637890",
      cedula: "0000123321",
      informaciondisp: true,
      operador: {
        id_operador: 5,
        nombre: "Operador1234",
        usuario: "Operador1234",
        contrasena: "0000"
      }
    }
  ];

  constructor() { }

  getDomiciliarios(): Domiciliario[] {
    return this.domiciliarios;
  }
}
