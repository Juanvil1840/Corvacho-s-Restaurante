import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OperadorService } from '../../service/operador.service';
import { Operador } from '../../models/operador.model';
import { OperadorTablaComponent } from './components/operador-tabla/operador-tabla.component';

@Component({
  selector: 'app-lista-operadores',
  standalone: true,
  imports: [CommonModule, RouterLink, OperadorTablaComponent],
  templateUrl: './lista-operadores.component.html',
  styleUrl: './lista-operadores.component.scss'
})
export class ListaOperadoresComponent implements OnInit {

  operadores: Operador[] = [];
  cargando = false;
  error = '';

  constructor(private operadorService: OperadorService) {}

  ngOnInit(): void {
    this.cargarOperadores();
  }

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

  eliminarOperador(id: number): void {
    if (confirm('¿Eliminar este operador?')) {
      this.operadorService.eliminarOperador(id).subscribe({
        next: () => {
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

