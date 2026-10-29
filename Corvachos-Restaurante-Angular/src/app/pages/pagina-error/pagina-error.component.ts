import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ErrorMensajeComponent } from './components/error-mensaje/error-mensaje.component';

@Component({
  selector: 'app-pagina-error',
  standalone: true,
  imports: [CommonModule, RouterLink, ErrorMensajeComponent],
  templateUrl: './pagina-error.component.html',
  styleUrl: './pagina-error.component.scss'
})
export class PaginaErrorComponent implements OnInit {

  mensaje = 'La página que buscas no existe o ocurrió un problema inesperado.';

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    const msg = this.route.snapshot.queryParamMap.get('mensaje');
    if (msg) {
      this.mensaje = msg;
    }
  }
}