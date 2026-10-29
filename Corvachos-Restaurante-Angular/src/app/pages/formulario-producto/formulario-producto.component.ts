import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductoService } from '../../service/producto.service';
import { Producto } from '../../models/producto.model';
import { Categoria } from '../../models/categoria.model';
import { CategoriaService } from '../../service/categoria.service';
import { ProductoCamposComponent } from './components/producto-campos/producto-campos.component';

// Página de formulario para registrar o editar un producto del menú
@Component({
  selector: 'app-formulario-producto',
  standalone: true,
  imports: [CommonModule, FormsModule, ProductoCamposComponent],
  templateUrl: './formulario-producto.component.html',
  styleUrl: './formulario-producto.component.scss'
})
export class FormularioProductoComponent implements OnInit {

  // Datos del producto vinculados a los controles del formulario
  producto: Producto = {
    id: 0,
    nombre: '',
    precio: 0,
    descripcion: '',
    imagen: '',
    disponible: true,
    categoria: { id: 0, nombre: '' }
  };

  // Lista de categorías disponibles y estados del formulario
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

  // Carga las categorías y, si existe un ID en la URL, consulta los datos del producto a editar
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

  // Actualiza el objeto categoría del producto según el ID elegido en el selector
  actualizarCategoria(): void {
    const categoriaId = Number(this.producto.categoria.id);
    const categoriaSeleccionada = this.categorias.find(
      categoria => categoria.id === categoriaId
    );

    if (categoriaSeleccionada) {
      this.producto.categoria = categoriaSeleccionada;
    }
  }

  // Guarda el nuevo producto o actualiza el existente y redirige a la lista de productos
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

  // Cancela la edición y regresa al listado de productos
  cancelar(): void {
    this.router.navigate(['/productos']);
  }
}
