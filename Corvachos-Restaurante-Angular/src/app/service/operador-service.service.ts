import { Injectable } from '@angular/core';
import { Operador } from '../models/operador.model';

@Injectable({
  providedIn: 'root'
})
export class OperadorServiceService {

  private operadores: Operador[] = [
    {
      id_operador: 1,
      nombre: "Kika Nieto",
      usuario: "KikaNieto_",
      contrasena: "1234"
    },
    {
      id_operador: 2,
      nombre: "Nicolas Almendra",
      usuario: "NicoAla_08",
      contrasena: "4322"
    },
    {
      id_operador: 3,
      nombre: "Mafe Cruz",
      usuario: "Mafecruz_0909",
      contrasena: "0987"
    },
    {
      id_operador: 4,
      nombre: "Samuel Corredor",
      usuario: "SamCor__",
      contrasena: "6392"
    },
    {
      id_operador: 5,
      nombre: "Operador1234",
      usuario: "Operador1234",
      contrasena: "0000"
    }
  ];

  constructor() { }

  getOperadores(): Operador[] {
    return this.operadores;
  }

}