import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ProductoServiceService } from '../../service/producto-service.service';
import { AdicionalService } from '../../service/adicional.service';
import { Producto } from '../../models/producto.model';
import { Adicional } from '../../models/adicional.model';
import { DetalleInfoComponent } from './components/detalle-info/detalle-info.component';
import { DetalleImagenComponent } from './components/detalle-imagen/detalle-imagen.component';

@Component({
  selector: 'app-detalle-producto',
  standalone: true,
  imports: [CommonModule, DetalleInfoComponent, DetalleImagenComponent],
  templateUrl: './detalle-producto.component.html',
  styleUrl: './detalle-producto.component.scss'
})
export class DetalleProductoComponent implements OnInit {

  producto: Producto | undefined;
  adicionales: Adicional[] = [];
  error = '';

  constructor(
    private productoService: ProductoServiceService,
    private adicionalService: AdicionalService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      this.error = 'No se encontró el identificador del producto.';
      return;
    }

    this.productoService.getProductoById(Number(id)).subscribe({
      next: (producto) => {
        this.producto = producto;

        const categoriaId = producto.categoria?.id;

        if (categoriaId !== undefined) {
          this.adicionalService.getAdicionalesDisponiblesPorCategoria(categoriaId).subscribe({
            next: (adicionales) => {
              this.adicionales = adicionales;
            },
            error: (error) => {
              console.error('Error al consultar los adicionales:', error);
              this.adicionales = [];
            }
          });
        }
      },
      error: (error) => {
        console.error('Error al consultar el producto:', error);
        this.error = 'No fue posible cargar el detalle del producto.';
      }
    });
  }
}
