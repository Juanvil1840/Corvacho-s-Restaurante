import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Operador } from '../../models/operador.model';
import { AuthService } from '../../service/auth.service';
import { OperadorOpcionesComponent } from './components/operador-opciones/operador-opciones.component';
import { OperadorPerfilComponent } from './components/operador-perfil/operador-perfil.component';

// Componente principal del portal de trabajo para operarios
@Component({
  selector: 'app-portal-operario',
  standalone: true,
  imports: [CommonModule, OperadorPerfilComponent, OperadorOpcionesComponent],
  templateUrl: './portal-operario.component.html',
  styleUrl: './portal-operario.component.scss'
})
export class PortalOperarioComponent implements OnInit {

  // Nombre del operador autenticado
  nombreOperador = '';

  constructor(private router: Router, private authService: AuthService) {}

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

  // Cierra la sesión (borra el token JWT y los datos) y vuelve al inicio
  cerrarSesion(): void {
    this.authService.cerrarSesion();
    this.router.navigate(['/']);
  }
}
