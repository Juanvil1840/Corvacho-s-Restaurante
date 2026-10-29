import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Administrador } from '../../models/administrador.model';

// Importamos los subcomponentes
import { AdminPerfilComponent } from './components/admin-perfil/admin-perfil.component';
import { AdminOpcionesComponent } from './components/admin-opciones/admin-opciones.component';

@Component({
  selector: 'app-portal-admin',
  standalone: true,
  imports: [CommonModule, AdminPerfilComponent, AdminOpcionesComponent],
  templateUrl: './portal-admin.component.html',
  styleUrl: './portal-admin.component.scss'
})
export class PortalAdminComponent implements OnInit {

  nombreAdmin = '';
  idAdmin = 0; 

  constructor(private router: Router) {}

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

  cerrarSesion(): void {
    localStorage.removeItem('administrador');
    this.router.navigate(['/login-admin']);
  }
}