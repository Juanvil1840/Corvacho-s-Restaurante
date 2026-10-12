import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdministradorService } from '../../service/administrador.service';
import { Administrador } from '../../models/administrador.model';
import { AdministradorTablaComponent } from './components/administrador-tabla/administrador-tabla.component';

@Component({
  selector: 'app-lista-administradores',
  standalone: true,
  imports: [CommonModule, RouterLink, AdministradorTablaComponent],
  templateUrl: './lista-administradores.component.html',
  styleUrl: './lista-administradores.component.scss'
})
export class ListaAdministradoresComponent implements OnInit {

  administradores: Administrador[] = [];
  cargando = false;
  error = '';

  constructor(private administradorService: AdministradorService) {}

  ngOnInit(): void {
    this.cargarAdministradores();
  }

  cargarAdministradores(): void {
    this.cargando = true;
    this.error = '';

    this.administradorService.getAdministradores().subscribe({
      next: (administradores) => {
        this.administradores = administradores;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar los administradores:', error);
        this.error = 'No fue posible cargar los administradores. Intenta nuevamente.';
        this.cargando = false;
      }
    });
  }

  eliminarAdministrador(id: number): void {
    if (confirm('¿Eliminar este administrador?')) {
      this.administradorService.eliminarAdministrador(id).subscribe({
        next: () => {
          this.cargarAdministradores();
        },
        error: (error) => {
          console.error('Error al eliminar el administrador:', error);
          this.error = 'No fue posible eliminar el administrador.';
        }
      });
    }
  }
}

