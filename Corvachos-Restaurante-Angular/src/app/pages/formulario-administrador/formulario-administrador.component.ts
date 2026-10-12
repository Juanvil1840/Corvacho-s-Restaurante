import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AdministradorService } from '../../service/administrador.service';
import { Administrador } from '../../models/administrador.model';
import { AdministradorCamposComponent } from './components/administrador-campos/administrador-campos.component';

@Component({
  selector: 'app-formulario-administrador',
  standalone: true,
  imports: [CommonModule, FormsModule, AdministradorCamposComponent],
  templateUrl: './formulario-administrador.component.html',
  styleUrl: './formulario-administrador.component.scss'
})
export class FormularioAdministradorComponent implements OnInit {

  administrador: Administrador = {
    id_administrador: 0,
    nombre: ''
  };

  editando = false;
  guardando = false;
  error = '';

  constructor(
    private administradorService: AdministradorService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.editando = true;

      this.administradorService.getAdministradorById(Number(id)).subscribe({
        next: (administrador) => {
          this.administrador = { ...administrador };
        },
        error: (error) => {
          console.error('Error al cargar el administrador:', error);
          this.error = 'No fue posible cargar el administrador.';
        }
      });
    }
  }

  guardar(): void {
    if (!this.administrador.nombre.trim()) {
      this.error = 'El nombre del administrador es obligatorio.';
      return;
    }

    this.guardando = true;
    this.error = '';

    const peticion = this.editando
      ? this.administradorService.actualizarAdministrador(this.administrador)
      : this.administradorService.agregarAdministrador(this.administrador);

    peticion.subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/administradores']);
      },
      error: (error) => {
        console.error('Error al guardar el administrador:', error);
        this.guardando = false;
        this.error = error?.error?.message || 'No fue posible guardar el administrador. Intenta nuevamente.';
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/administradores']);
  }
}

