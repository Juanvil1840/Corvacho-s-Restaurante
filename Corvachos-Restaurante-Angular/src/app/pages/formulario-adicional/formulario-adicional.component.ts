import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AdicionalService } from '../../service/adicional.service';
import { CategoriaService } from '../../service/categoria.service';
import { Adicional } from '../../models/adicional.model';
import { Categoria } from '../../models/categoria.model';
import { AdicionalCamposComponent } from './components/adicional-campos/adicional-campos.component';

@Component({
  selector: 'app-formulario-adicional',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, AdicionalCamposComponent],
  templateUrl: './formulario-adicional.component.html',
  styleUrl: './formulario-adicional.component.scss'
})
export class FormularioAdicionalComponent implements OnInit {

  adicional: Adicional = {
    id_adicional: 0,
    nombre: '',
    precio: 0,
    disponible: true,
    categoria: { id: 0, nombre: '' }
  };

  categorias: Categoria[] = [];
  editando = false;
  guardando = false;
  error = '';

  constructor(
    private adicionalService: AdicionalService,
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

      this.adicionalService.getAdicionalById(Number(id)).subscribe({
        next: (adicional) => {
          this.adicional = { ...adicional };
        },
        error: (error) => {
          console.error('Error al cargar el adicional:', error);
          this.error = 'No fue posible cargar el adicional.';
        }
      });
    }
  }

  actualizarCategoria(): void {
    const categoriaId = Number(this.adicional.categoria.id);
    const categoriaSeleccionada = this.categorias.find(
      categoria => categoria.id === categoriaId
    );

    if (categoriaSeleccionada) {
      this.adicional.categoria = categoriaSeleccionada;
    }
  }

  guardar(): void {
    this.actualizarCategoria();
    this.guardando = true;
    this.error = '';

    const peticion = this.editando
      ? this.adicionalService.actualizarAdicional(this.adicional)
      : this.adicionalService.agregarAdicional(this.adicional);

    peticion.subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/adicionales']);
      },
      error: (error) => {
        console.error('Error al guardar el adicional:', error);
        this.guardando = false;
        this.error = 'No fue posible guardar el adicional. Intenta nuevamente.';
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/adicionales']);
  }
}