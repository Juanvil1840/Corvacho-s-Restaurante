import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AdministradorService } from '../../service/administrador.service';
import { Administrador } from '../../models/administrador.model';
import { AdministradorTablaComponent } from './components/administrador-tabla/administrador-tabla.component';

// Componente para listar y gestionar los administradores del sistema
@Component({
  selector: 'app-lista-administradores',
  standalone: true,
  imports: [CommonModule, RouterLink, AdministradorTablaComponent],
  templateUrl: './lista-administradores.component.html',
  styleUrl: './lista-administradores.component.scss'
})
export class ListaAdministradoresComponent implements OnInit {

  // Lista de administradores y variables de estado de carga y error
  administradores: Administrador[] = [];
  cargando = false;
  error = '';

  constructor(
    private administradorService: AdministradorService,
    private router: Router
  ) {}

  // Inicializa el componente cargando la lista de administradores
  ngOnInit(): void {
    this.cargarAdministradores();
  }

  // Obtiene todos los administradores desde el backend
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

  // Redirige al formulario de creación de un nuevo operador
  nuevoOperador(): void {
    this.router.navigate(['/operadores/nuevo']);
  }

  // Confirma y elimina un administrador por su ID
  eliminarAdministrador(id: number): void {
    if (confirm('¿Eliminar este administrador?')) {
      this.administradorService.eliminarAdministrador(id).subscribe({
        next: () => {
          // Recarga la lista después de eliminar
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
