import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DomiciliarioService } from '../../service/domiciliario.service';
import { Domiciliario } from '../../models/domiciliario.model';
import { DomiciliarioTablaComponent } from './components/domiciliario-tabla/domiciliario-tabla.component';

// Componente para listar y gestionar los domiciliarios del restaurante
@Component({
  selector: 'app-lista-domiciliarios',
  standalone: true,
  imports: [CommonModule, RouterLink, DomiciliarioTablaComponent],
  templateUrl: './lista-domiciliarios.component.html',
  styleUrl: './lista-domiciliarios.component.scss'
})
export class ListaDomiciliariosComponent implements OnInit {

  // Lista de domiciliarios y estados de carga y error
  domiciliarios: Domiciliario[] = [];
  cargando = false;
  error = '';

  constructor(private domiciliarioService: DomiciliarioService) {}

  // Carga la lista de domiciliarios al iniciar el componente
  ngOnInit(): void {
    this.cargarDomiciliarios();
  }

  // Obtiene los domiciliarios registrados desde el backend
  cargarDomiciliarios(): void {
    this.cargando = true;
    this.error = '';

    this.domiciliarioService.getDomiciliarios().subscribe({
      next: (domiciliarios) => {
        this.domiciliarios = domiciliarios;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar los domiciliarios:', error);
        this.error = 'No fue posible cargar los domiciliarios. Intenta nuevamente.';
        this.cargando = false;
      }
    });
  }

  // Confirma con el usuario y elimina un domiciliario por su ID
  eliminarDomiciliario(id: number): void {
    if (confirm('¿Eliminar este domiciliario?')) {
      this.domiciliarioService.eliminarDomiciliario(id).subscribe({
        next: () => {
          // Recarga la lista tras eliminar el domiciliario
          this.cargarDomiciliarios();
        },
        error: (error) => {
          console.error('Error al eliminar el domiciliario:', error);
          this.error = 'No fue posible eliminar el domiciliario.';
        }
      });
    }
  }
}
