import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProductoServiceService } from '../../service/producto-service.service';
import { CategoriaServiceService } from '../../service/categoria-service.service';
import { Producto } from '../../models/producto.model';
import { Categoria } from '../../models/categoria.model';
import { FooterComponent } from '../../shared/footer/footer.component';

@Component({
  selector: 'app-formulario-producto',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './formulario-producto.component.html',
  styleUrl: './formulario-producto.component.scss'
})
export class FormularioProductoComponent implements OnInit {

  producto: Producto = {
    id: 0,
    nombre: '',
    precio: 0,
    descripcion: '',
    imagen: '',
    disponible: true,
    categoria: { id: 0, nombre: '' }
  };

  categorias: Categoria[] = [];
  editando: boolean = false;

  constructor(
    private productoService: ProductoServiceService,
    private categoriaService: CategoriaServiceService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Cargar todas las categorias
    this.categorias = this.categoriaService.getCategorias();

    // Verificar si estamos editando
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editando = true;
      const productoExistente = this.productoService.getProductoById(Number(id));
      if (productoExistente) {
        this.producto = { ...productoExistente };
      }
    }
  }
  actualizarCategoria(): void {
    const catSeleccionada = this.categorias.find(c => c.id === Number(this.producto.categoria.id));
    if (catSeleccionada) {
      this.producto.categoria = catSeleccionada;
    }
  }
    guardar(): void {
    this.actualizarCategoria(); 
    if (this.editando) {
      this.productoService.actualizarProducto(this.producto);
    } else {
      this.productoService.agregarProducto(this.producto);
    }
    this.router.navigate(['/productos']);
  }

  cancelar(): void {
    this.router.navigate(['/productos']);
  }
}