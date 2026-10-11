import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CategoriaService } from '../../service/categoria.service';
import { Categoria } from '../../models/categoria.model';
import { CategoriaTablaComponent } from './components/categoria-tabla/categoria-tabla.component';

@Component({
  selector: 'app-lista-categorias',
  standalone: true,
  imports: [CommonModule, RouterLink, CategoriaTablaComponent],
  templateUrl: './lista-categorias.component.html',
  styleUrl: './lista-categorias.component.scss'
})
export class ListaCategoriasComponent implements OnInit {

  categorias: Categoria[] = [];
  cargando = false;
  error = '';

  constructor(private categoriaService: CategoriaService) {}

  ngOnInit(): void {
    this.cargarCategorias();
  }

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

  eliminarCategoria(id: number): void {
    if (confirm('¿Eliminar esta categoría?')) {
      this.categoriaService.eliminarCategoria(id).subscribe({
        next: () => {
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