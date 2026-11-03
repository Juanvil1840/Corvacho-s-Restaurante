import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CategoriaService } from '../../service/categoria.service';
import { Categoria } from '../../models/categoria.model';
import { CategoriaCamposComponent } from './components/categoria-campos/categoria-campos.component';

// Página de formulario para crear o editar una categoría de productos
@Component({
  selector: 'app-formulario-categoria',
  standalone: true,
  imports: [CommonModule, FormsModule, CategoriaCamposComponent],
  templateUrl: './formulario-categoria.component.html',
  styleUrl: './formulario-categoria.component.scss'
})
export class FormularioCategoriaComponent implements OnInit {

  // Modelo de la categoría vinculada al formulario
  categoria: Categoria = {
    id: 0,
    nombre: ''
  };

  // Estado de modo edición, carga de guardado y errores
  editando = false;
  guardando = false;
  error = '';

  constructor(
    private categoriaService: CategoriaService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  // Verifica si se está editando una categoría existente para cargar sus datos
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.editando = true;

      this.categoriaService.getCategoriaById(Number(id)).subscribe({
        next: (categoria) => {
          this.categoria = { ...categoria };
        },
        error: (error) => {
          console.error('Error al cargar la categoría:', error);
          this.error = 'No fue posible cargar la categoría.';
        }
      });
    }
  }

  // Crea o actualiza la categoría en el backend y redirige al listado
  guardar(): void {
    this.guardando = true;
    this.error = '';

    const peticion = this.editando
      ? this.categoriaService.actualizarCategoria(this.categoria)
      : this.categoriaService.agregarCategoria(this.categoria);

    peticion.subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/categorias']);
      },
      error: (error) => {
        console.error('Error al guardar la categoría:', error);
        this.guardando = false;
        this.error = 'No fue posible guardar la categoría. Intenta nuevamente.';
      }
    });
  }

  // Cancela la operación y vuelve a la lista de categorías
  cancelar(): void {
    this.router.navigate(['/categorias']);
  }
}
