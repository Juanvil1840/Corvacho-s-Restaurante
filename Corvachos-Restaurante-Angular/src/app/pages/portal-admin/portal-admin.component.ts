import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Administrador } from '../../models/administrador.model';
import { AuthService } from '../../service/auth.service';
import { AdminOpcionesComponent } from './components/admin-opciones/admin-opciones.component';
import { AdminPerfilComponent } from './components/admin-perfil/admin-perfil.component';

// Componente principal del panel de administración
@Component({
  selector: 'app-portal-admin',
  standalone: true,
  imports: [CommonModule, AdminPerfilComponent, AdminOpcionesComponent],
  templateUrl: './portal-admin.component.html',
  styleUrl: './portal-admin.component.scss'
})
export class PortalAdminComponent implements OnInit {

  // Datos básicos del administrador con sesión activa
  nombreAdmin = '';
  idAdmin = 0; 

  constructor(private router: Router, private authService: AuthService) {}

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

  // Cierra la sesión (borra el token JWT y los datos) y vuelve al inicio
  cerrarSesion(): void {
    this.authService.cerrarSesion();
    this.router.navigate(['/']);
  }
}
