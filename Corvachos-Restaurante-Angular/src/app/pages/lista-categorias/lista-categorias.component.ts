import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CategoriaService } from '../../service/categoria.service';
import { Categoria } from '../../models/categoria.model';
import { CategoriaTablaComponent } from './components/categoria-tabla/categoria-tabla.component';

// Componente para listar y administrar las categorías de productos
@Component({
  selector: 'app-lista-categorias',
  standalone: true,
  imports: [CommonModule, RouterLink, CategoriaTablaComponent],
  templateUrl: './lista-categorias.component.html',
  styleUrl: './lista-categorias.component.scss'
})
export class ListaCategoriasComponent implements OnInit {

  // Lista de categorías y estados de carga y error
  categorias: Categoria[] = [];
  cargando = false;
  error = '';

  constructor(private categoriaService: CategoriaService) {}

  // Carga las categorías al iniciar el componente
  ngOnInit(): void {
    this.cargarCategorias();
  }

  // Obtiene las categorías desde el servicio correspondiente
  cargarCategorias(): void {
    this.cargando = true;
    this.error = '';

    this.categoriaService.getCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar las categorías:', error);
        this.error = 'No fue posible cargar las categorías. Intenta nuevamente.';
        this.cargando = false;
      }
    });
  }

  // Solicita confirmación y elimina una categoría por su ID
  eliminarCategoria(id: number): void {
    if (confirm('¿Eliminar esta categoría?')) {
      this.categoriaService.eliminarCategoria(id).subscribe({
        next: () => {
          // Actualiza el listado tras la eliminación
          this.cargarCategorias();
        },
        error: (error) => {
          console.error('Error al eliminar la categoría:', error);
          this.error = 'No fue posible eliminar la categoría.';
        }
      });
    }
  }
}
