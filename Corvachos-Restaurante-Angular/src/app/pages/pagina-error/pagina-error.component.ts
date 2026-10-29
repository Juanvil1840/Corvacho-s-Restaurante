import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

// Componente para mostrar una página de error general o mensajes de excepción personalizados
@Component({
  selector: 'app-pagina-error',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './pagina-error.component.html',
  styleUrl: './pagina-error.component.scss'
})
export class PaginaErrorComponent implements OnInit {

  // Mensaje de error por defecto cuando no se especifica uno en la ruta
  mensaje = 'La página que buscas no existe o ocurrió un problema inesperado.';

  constructor(private route: ActivatedRoute) {}

  // Verifica si viene un mensaje personalizado en los parámetros de consulta de la URL
  ngOnInit(): void {
    const msg = this.route.snapshot.queryParamMap.get('mensaje');
    if (msg) {
      this.mensaje = msg;
    }
  }
}
