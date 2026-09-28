import { Carrito } from "./carrito.model";
import { Producto } from "./producto.model";

export interface ProductoCarrito {
    id_ProductoCarrito: number;
    cantidadP: number;
    producto: Producto;
    carrito: Carrito;
}