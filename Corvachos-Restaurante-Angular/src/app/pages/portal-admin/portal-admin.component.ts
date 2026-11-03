import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Administrador } from '../../models/administrador.model';

// Componente principal del panel de administración
@Component({
  selector: 'app-portal-admin',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './portal-admin.component.html',
  styleUrl: './portal-admin.component.scss'
})
export class PortalAdminComponent implements OnInit {

  // Datos básicos del administrador con sesión activa
  nombreAdmin = '';
  idAdmin = 0; 

  constructor(private router: Router) {}

  // Verifica que exista una sesión de administrador activa al cargar el portal
  ngOnInit(): void {
    const adminStr = localStorage.getItem('administrador');
    if (!adminStr) {
      this.router.navigate(['/login-admin']);
      return;
    }
    const admin: Administrador = JSON.parse(adminStr);
    this.nombreAdmin = admin.nombre;
    this.idAdmin = admin.id_administrador;
  }

  // Elimina la sesión del administrador y redirige al login
  cerrarSesion(): void {
    localStorage.removeItem('administrador');
    this.router.navigate(['/login-admin']);
  }
}
