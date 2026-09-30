import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProductoServiceService } from '../../service/producto-service.service';
import { AdicionalServiceService } from '../../service/adicional-service.service';
import { Producto } from '../../models/producto.model';
import { Adicional } from '../../models/adicional.model';
import { DetalleInfoComponent } from './components/detalle-info/detalle-info.component';
import { DetalleImagenComponent } from './components/detalle-imagen/detalle-imagen.component';

@Component({
  selector: 'app-detalle-producto',
  standalone: true,
  imports: [CommonModule, RouterLink, DetalleInfoComponent, DetalleImagenComponent],
  templateUrl: './detalle-producto.component.html',
  styleUrl: './detalle-producto.component.scss'
})
export class DetalleProductoComponent implements OnInit {

  producto: Producto | undefined;
  adicionales: Adicional[] = [];

  constructor(
    private productoService: ProductoServiceService,
    private adicionalService: AdicionalServiceService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.producto = this.productoService.getProductoById(Number(id));

      if (this.producto && this.producto.categoria) {
        const categoriaId = this.producto.categoria.id;
        this.adicionales = this.adicionalService.getAdicionales().filter(
          a => a.categoria && a.categoria.id === categoriaId && a.disponible
        );
      }
    }
  }

}