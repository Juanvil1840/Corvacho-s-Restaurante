import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ProductoService } from '../../service/producto.service';
import { AdicionalService } from '../../service/adicional.service';
import { Producto } from '../../models/producto.model';
import { Adicional } from '../../models/adicional.model';
import { DetalleInfoComponent } from './components/detalle-info/detalle-info.component';
import { DetalleImagenComponent } from './components/detalle-imagen/detalle-imagen.component';

// Página de detalle de un producto donde se carga su información y los adicionales de su categoría
@Component({
  selector: 'app-detalle-producto',
  standalone: true,
  imports: [CommonModule, DetalleInfoComponent, DetalleImagenComponent],
  templateUrl: './detalle-producto.component.html',
  styleUrl: './detalle-producto.component.scss'
})
export class DetalleProductoComponent implements OnInit {

  // Producto consultado, sus adicionales disponibles y mensaje de error
  producto: Producto | undefined;
  adicionales: Adicional[] = [];
  error = '';

  constructor(
    private productoService: ProductoService,
    private adicionalService: AdicionalService,
    private route: ActivatedRoute
  ) {}

  // Carga el producto por su ID desde la URL y luego consulta los adicionales de su categoría
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

        // Si el producto tiene categoría, busca los adicionales disponibles para esa categoría
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
