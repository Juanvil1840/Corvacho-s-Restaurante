import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Operador } from '../../models/operador.model';

// Componente principal del portal de trabajo para operarios
@Component({
  selector: 'app-portal-operario',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './portal-operario.component.html',
  styleUrl: './portal-operario.component.scss'
})
export class PortalOperarioComponent implements OnInit {

  // Nombre del operador autenticado
  nombreOperador = '';

  constructor(private router: Router) {}

  // Comprueba si hay un operador en sesión o redirige al login correspondiente
  ngOnInit(): void {
    const operadorStr = localStorage.getItem('operador');
    if (!operadorStr) {
      this.router.navigate(['/login-operario']);
      return;
    }
    const operador: Operador = JSON.parse(operadorStr);
    this.nombreOperador = operador.nombre;
  }

  // Cierra la sesión del operador y navega a la pantalla de login
  cerrarSesion(): void {
    localStorage.removeItem('operador');
    this.router.navigate(['/login-operario']);
  }
}
