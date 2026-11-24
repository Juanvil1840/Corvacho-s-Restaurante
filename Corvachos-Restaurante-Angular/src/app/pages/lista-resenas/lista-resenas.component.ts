import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Resena } from '../../models/resena.model';
import { ResenaService } from '../../service/resena.service';

@Component({
  selector: 'app-lista-resenas',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './lista-resenas.component.html',
  styleUrl: './lista-resenas.component.scss'
})
export class ListaResenasComponent implements OnInit {
  resenas: Resena[] = [];
  cargando = true;
  error = '';

  constructor(private resenaService: ResenaService) {}

  ngOnInit(): void {
    this.resenaService.getTodas().subscribe({
      next: resenas => { this.resenas = resenas; this.cargando = false; },
      error: err => {
        console.error('Error al cargar reseñas:', err);
        this.error = 'No fue posible cargar las reseñas.';
        this.cargando = false;
      }
    });
  }
}
