import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Operador } from '../../models/operador.model';

// Importamos los subcomponentes
import { OperadorPerfilComponent } from './components/operador-perfil/operador-perfil.component';
import { OperadorOpcionesComponent } from './components/operador-opciones/operador-opciones.component';

@Component({
  selector: 'app-portal-operario',
  standalone: true,
  imports: [CommonModule, OperadorPerfilComponent, OperadorOpcionesComponent],
  templateUrl: './portal-operario.component.html',
  styleUrl: './portal-operario.component.scss'
})
export class PortalOperarioComponent implements OnInit {

  nombreOperador = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    const operadorStr = localStorage.getItem('operador');
    if (!operadorStr) {
      this.router.navigate(['/login-operario']);
      return;
    }
    const operador: Operador = JSON.parse(operadorStr);
    this.nombreOperador = operador.nombre;
  }

  cerrarSesion(): void {
    localStorage.removeItem('operador');
    this.router.navigate(['/login-operario']);
  }
}