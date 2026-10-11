import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdicionalService } from '../../service/adicional.service';
import { Adicional } from '../../models/adicional.model';
import { AdicionalTablaComponent } from './components/adicional-tabla/adicional-tabla.component';

@Component({
  selector: 'app-lista-adicionales',
  standalone: true,
  imports: [CommonModule, RouterLink, AdicionalTablaComponent],
  templateUrl: './lista-adicionales.component.html',
  styleUrl: './lista-adicionales.component.scss'
})
export class ListaAdicionalesComponent implements OnInit {

  adicionales: Adicional[] = [];
  cargando = false;
  error = '';

  constructor(private adicionalService: AdicionalService) {}

  ngOnInit(): void {
    this.cargarAdicionales();
  }

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

  eliminarAdicional(id: number): void {
    if (confirm('¿Eliminar este adicional?')) {
      this.adicionalService.eliminarAdicional(id).subscribe({
        next: () => {
          this.cargarAdicionales();
        },
        error: (error) => {
          console.error('Error al eliminar el adicional:', error);
          this.error = 'No fue posible eliminar el adicional.';
        }
      });
    }
  }
}