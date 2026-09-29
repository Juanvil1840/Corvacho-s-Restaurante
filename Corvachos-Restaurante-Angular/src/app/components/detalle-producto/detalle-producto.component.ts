import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProductoServiceService } from '../../service/producto-service.service';
import { Producto } from '../../models/producto.model';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-detalle-producto',
  standalone: true,
  imports: [CommonModule, RouterLink, FooterComponent],
  templateUrl: './detalle-producto.component.html',
  styleUrl: './detalle-producto.component.scss'
})
export class DetalleProductoComponent implements OnInit {

  producto: Producto | undefined;

  constructor(
    private productoService: ProductoServiceService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.producto = this.productoService.getProductoById(Number(id));
    }
  }
}