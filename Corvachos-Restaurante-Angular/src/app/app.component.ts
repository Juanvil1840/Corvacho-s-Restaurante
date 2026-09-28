import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ProductoServiceService } from './service/producto-service.service';
import { AdicionalServiceService } from './service/adicional-service.service';
import { CategoriaServiceService } from './service/categoria-service.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'Corvachos-Restaurante';
  productos: any[] = [];
  adicionales: any[] = [];
  categorias: any[] = [];

  constructor( private productoService: ProductoServiceService, private adicionalService: AdicionalServiceService, private categoriaService: CategoriaServiceService) {
  this.productos = this.productoService.getProductos();
  this.adicionales = this.adicionalService.getAdicionales();
  this.categorias = this.categoriaService.getCategorias();
  }
}
