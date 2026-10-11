import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CategoriaService } from '../../service/categoria.service';
import { Categoria } from '../../models/categoria.model';
import { CategoriaCamposComponent } from './components/categoria-campos/categoria-campos.component';

@Component({
  selector: 'app-formulario-categoria',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, CategoriaCamposComponent],
  templateUrl: './formulario-categoria.component.html',
  styleUrl: './formulario-categoria.component.scss'
})
export class FormularioCategoriaComponent implements OnInit {

  categoria: Categoria = {
    id: 0,
    nombre: ''
  };

  editando = false;
  guardando = false;
  error = '';

  constructor(
    private categoriaService: CategoriaService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

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

  cancelar(): void {
    this.router.navigate(['/categorias']);
  }
}