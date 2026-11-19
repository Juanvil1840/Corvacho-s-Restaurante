import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CategoriaService } from '../../service/categoria.service';
import { Categoria } from '../../models/categoria.model';
import { CategoriaTablaComponent } from './components/categoria-tabla/categoria-tabla.component';
import { AlertaService } from '../../service/alerta.service';

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

  constructor(
    private categoriaService: CategoriaService,
    private alertaService: AlertaService
  ) {}

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
  async eliminarCategoria(id: number): Promise<void> {
    const confirmado = await this.alertaService.confirmar(
      '¿Estás seguro de que deseas eliminar esta categoría?',
      'Eliminar Categoría'
    );

    if (!confirmado) return;

    this.error = '';

    this.categoriaService.eliminarCategoria(id).subscribe({
      next: () => {
        this.alertaService.exito('La categoría ha sido eliminada correctamente.');
        this.cargarCategorias();
      },
      error: (error) => {
        console.error('Error al eliminar la categoría:', error);
        // Si el backend devuelve el mensaje de por qué no se puede eliminar, lo mostramos
        const mensajeError = error.error?.mensaje || 'No fue posible eliminar la categoría.';
        this.alertaService.error(mensajeError);
      }
    });
  }
}