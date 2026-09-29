import { Injectable } from '@angular/core';
import { Adicional } from '../models/adicional.model';

@Injectable({
  providedIn: 'root'
})
export class AdicionalServiceService {

  private adicionales: Adicional[] = [
    {
      id_adicional: 1,
      nombre: "Queso Extra",
      precio: 5500,
      disponible: true,
      categoria: {
        id: 2,
        nombre: "Pastas"
      }
    },
    {
      id_adicional: 2,
      nombre: "Aros de cebolla",
      precio: 10500,
      disponible: true,
      categoria: {
        id: 2,
        nombre: "Pastas"
      }
    },
    {
      id_adicional: 3,
      nombre: "Tocineta",
      precio: 8500,
      disponible: true,
      categoria: {
        id: 2,
        nombre: "Pastas"
      }
    },
    {
      id_adicional: 4,
      nombre: "Huevos de codorniz",
      precio: 3500,
      disponible: true,
      categoria: {
        id: 1,
        nombre: "Entradas"
      }
    },
    {
      id_adicional: 5,
      nombre: "Salsa de la casa",
      precio: 4500,
      disponible: true,
      categoria: {
        id: 2,
        nombre: "Pastas"
      }
    },
    {
      id_adicional: 6,
      nombre: "Salsa gratinada",
      precio: 4800,
      disponible: true,
      categoria: {
        id: 3,
        nombre: "Pescados y Mariscos"
      }
    },
    {
      id_adicional: 7,
      nombre: "ázucar",
      precio: 1000,
      disponible: true,
      categoria: {
        id: 7,
        nombre: "Bebidas"
      }
    },
    {
      id_adicional: 8,
      nombre: "Helado",
      precio: 6000,
      disponible: true,
      categoria: {
        id: 8,
        nombre: "Postres"
      }
    },
    {
      id_adicional: 9,
      nombre: "Pan de la casa",
      precio: 500,
      disponible: true,
      categoria: {
        id: 1,
        nombre: "Entradas"
      }
    },
    {
      id_adicional: 10,
      nombre: "BBQ",
      precio: 1000,
      disponible: true,
      categoria: {
        id: 4,
        nombre: "Carnes"
      }
    },
    {
      id_adicional: 11,
      nombre: "Queso parmesano",
      precio: 5800,
      disponible: true,
      categoria: {
        id: 6,
        nombre: "Pizzas"
      }
    },
    {
      id_adicional: 12,
      nombre: "chiles",
      precio: 7000,
      disponible: true,
      categoria: {
        id: 6,
        nombre: "Pizzas"
      }
    },
    {
      id_adicional: 13,
      nombre: "Vinagreta",
      precio: 2000,
      disponible: true,
      categoria: {
        id: 5,
        nombre: "Ensaladas"
      }
    }
  ];

  constructor() { }

  getAdicionales(): Adicional[] {
    return this.adicionales;
  }

}