import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdicionalService } from '../../service/adicional.service';
import { Adicional } from '../../models/adicional.model';
import { AdicionalTablaComponent } from './components/adicional-tabla/adicional-tabla.component';
import { AlertaService } from '../../service/alerta.service';

// Componente para listar y administrar los adicionales del restaurante
@Component({
  selector: 'app-lista-adicionales',
  standalone: true,
  imports: [CommonModule, RouterLink, AdicionalTablaComponent],
  templateUrl: './lista-adicionales.component.html',
  styleUrl: './lista-adicionales.component.scss'
})
export class ListaAdicionalesComponent implements OnInit {

  // Lista de adicionales obtenidos del servidor y estados de la vista
  adicionales: Adicional[] = [];
  cargando = false;
  error = '';

  constructor(
    private adicionalService: AdicionalService,
    private alertaService: AlertaService
  ) {}

  // Carga los adicionales al inicializar el componente
  ngOnInit(): void {
    this.cargarAdicionales();
  }

  // Consulta al servicio la lista de adicionales disponibles
  cargarAdicionales(): void {
    this.cargando = true;
    this.error = '';

    this.adicionalService.getAdicionales().subscribe({
      next: (adicionales) => {
        this.adicionales = adicionales;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar los adicionales:', error);
        this.error = 'No fue posible cargar los adicionales. Intenta nuevamente.';
        this.cargando = false;
      }
    });
  }

  // Solicita confirmación y elimina un adicional por su ID
  async eliminarAdicional(id: number): Promise<void> {
    const confirmado = await this.alertaService.confirmar(
      '¿Estás seguro de que deseas eliminar este adicional?',
      'Eliminar Adicional'
    );

    if (!confirmado) return;

    this.error = '';

    this.adicionalService.eliminarAdicional(id).subscribe({
      next: () => {
        this.alertaService.exito('El adicional ha sido eliminado correctamente.');
        this.cargarAdicionales();
      },
      error: (error) => {
        console.error('Error al eliminar el adicional:', error);
        const mensajeError = error.error?.mensaje || 'No fue posible eliminar el adicional.';
        this.alertaService.error(mensajeError);
      }
    });
  }
}