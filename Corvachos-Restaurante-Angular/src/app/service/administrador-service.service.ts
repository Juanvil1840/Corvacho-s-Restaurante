import { Injectable } from '@angular/core';
import { Administrador } from '../models/administrador.model';

@Injectable({
  providedIn: 'root'
})
export class AdministradorServiceService {

  private administradores: Administrador[] = [
    {
      id_administrador: 1,
      nombre: "Administrador 1"
    },
    {
      id_administrador: 2,
      nombre: "Administrador 2"
    },
    {
      id_administrador: 3,
      nombre: "Administrador 3"
    },
    {
      id_administrador: 4,
      nombre: "Administrador 4"
    },
    {
      id_administrador: 5,
      nombre: "Administrador 5"
    }
  ];

  constructor() { }

  getAdministradores(): Administrador[] {
    return this.administradores;
  }

}