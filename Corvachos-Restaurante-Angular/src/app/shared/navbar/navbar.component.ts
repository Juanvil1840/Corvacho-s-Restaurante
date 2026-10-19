import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit {

  esPaginaLogin: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Escucha cada cambio de ruta
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      const url = event.urlAfterRedirects || event.url;
      // Activa el botón solo si estamos en /login
      this.esPaginaLogin = url === '/login' || url.startsWith('/login?');
    });

    // Comprobar la ruta actual por si recargas la página en /login
    this.esPaginaLogin = this.router.url === '/login';
  }
}