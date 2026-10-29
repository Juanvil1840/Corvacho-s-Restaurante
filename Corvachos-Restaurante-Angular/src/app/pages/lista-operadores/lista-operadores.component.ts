import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OperadorService } from '../../service/operador.service';
import { Operador } from '../../models/operador.model';
import { OperadorTablaComponent } from './components/operador-tabla/operador-tabla.component';

// Componente para listar y administrar los operadores del sistema
@Component({
  selector: 'app-lista-operadores',
  standalone: true,
  imports: [CommonModule, RouterLink, OperadorTablaComponent],
  templateUrl: './lista-operadores.component.html',
  styleUrl: './lista-operadores.component.scss'
})
export class ListaOperadoresComponent implements OnInit {

  // Lista de operadores y variables de estado para la vista
  operadores: Operador[] = [];
  cargando = false;
  error = '';

  constructor(private operadorService: OperadorService) {}

  // Carga los operadores al inicializar el componente
  ngOnInit(): void {
    this.cargarOperadores();
  }

  // Obtiene la lista de operadores desde el servicio
  cargarOperadores(): void {
    this.cargando = true;
    this.error = '';

    this.operadorService.getOperadores().subscribe({
      next: (operadores) => {
        this.operadores = operadores;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar los operadores:', error);
        this.error = 'No fue posible cargar los operadores. Intenta nuevamente.';
        this.cargando = false;
      }
    });
  }

  // Solicita confirmación y elimina un operador por su ID
  eliminarOperador(id: number): void {
    if (confirm('¿Eliminar este operador?')) {
      this.operadorService.eliminarOperador(id).subscribe({
        next: () => {
          // Recarga la lista de operadores tras eliminar
          this.cargarOperadores();
        },
        error: (error) => {
          console.error('Error al eliminar el operador:', error);
          this.error = 'No fue posible eliminar el operador.';
        }
      });
    }
  }
}
