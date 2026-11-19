import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OperadorService } from '../../service/operador.service';
import { Operador } from '../../models/operador.model';
import { OperadorTablaComponent } from './components/operador-tabla/operador-tabla.component';
import { AlertaService } from '../../service/alerta.service';

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

  constructor(
    private operadorService: OperadorService,
    private alertaService: AlertaService
  ) {}

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
  async eliminarOperador(id: number): Promise<void> {
    const confirmado = await this.alertaService.confirmar(
      '¿Estás seguro de que deseas eliminar este operador?',
      'Eliminar Operador'
    );

    if (!confirmado) return;

    this.error = '';

    this.operadorService.eliminarOperador(id).subscribe({
      next: () => {
        this.alertaService.exito('El operador ha sido eliminado correctamente.');
        this.cargarOperadores();
      },
      error: (error) => {
        console.error('Error al eliminar el operador:', error);
        // Aquí se atrapa el mensaje del backend si el operador tiene pedidos asociados
        const mensajeError = error.error?.mensaje || 'No fue posible eliminar el operador (puede tener pedidos asociados).';
        this.alertaService.error(mensajeError);
      }
    });
  }
}