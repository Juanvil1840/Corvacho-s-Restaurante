import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductoService } from '../../service/producto.service';
import { Producto } from '../../models/producto.model';
import { Categoria } from '../../models/categoria.model';
import { CategoriaService } from '../../service/categoria.service';
import { ProductoCamposComponent } from './components/producto-campos/producto-campos.component';

@Component({
  selector: 'app-formulario-producto',
  standalone: true,
  imports: [CommonModule, FormsModule, ProductoCamposComponent],
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
  editando = false;
  guardando = false;
  error = '';

  constructor(
    private productoService: ProductoService,
    private categoriaService: CategoriaService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.categoriaService.getCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
      },
      error: (error) => {
        console.error('Error al cargar las categorías:', error);
        this.error = 'No fue posible cargar las categorías.';
      }
    });

    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.editando = true;

      this.productoService.getProductoById(Number(id)).subscribe({
        next: (producto) => {
          this.producto = { ...producto };
        },
        error: (error) => {
          console.error('Error al cargar el producto:', error);
          this.error = 'No fue posible cargar el producto.';
        }
      });
    }
  }

  actualizarCategoria(): void {
    const categoriaId = Number(this.producto.categoria.id);
    const categoriaSeleccionada = this.categorias.find(
      categoria => categoria.id === categoriaId
    );

    if (categoriaSeleccionada) {
      this.producto.categoria = categoriaSeleccionada;
    }
  }

  guardar(): void {
    this.actualizarCategoria();
    this.guardando = true;
    this.error = '';

    const peticion = this.editando
      ? this.productoService.actualizarProducto(this.producto)
      : this.productoService.agregarProducto(this.producto);

    peticion.subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/productos']);
      },
      error: (error) => {
        console.error('Error al guardar el producto:', error);
        this.guardando = false;
        this.error = 'No fue posible guardar el producto. Intenta nuevamente.';
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/productos']);
  }
}