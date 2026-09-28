import { Injectable } from '@angular/core';
import { Categoria } from '../models/categoria.model';

@Injectable({
  providedIn: 'root'
})
export class CategoriaServiceService {

  private categorias: Categoria[] = [
    {
      id: 1,
      nombre: "Entradas"
    },
    {
      id: 2,
      nombre: "Pastas"
    },
    {
      id: 3,
      nombre: "Pescados y Mariscos"
    },
    {
      id: 4,
      nombre: "Carnes"
    },
    {
      id: 5,
      nombre: "Ensaladas"
    },
    {
      id: 6,
      nombre: "Pizzas"
    },
    {
      id: 7,
      nombre: "Bebidas"
    },
    {
      id: 8,
      nombre: "Postres"
    }
  ];

  constructor() { }

  getCategorias(): Categoria[] {
  return this.categorias;
  }

}