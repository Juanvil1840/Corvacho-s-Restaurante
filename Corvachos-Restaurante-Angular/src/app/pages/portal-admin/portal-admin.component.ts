import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Administrador } from '../../models/administrador.model';

@Component({
  selector: 'app-portal-admin',
  standalone: true,
  imports: [CommonModule, RouterLink],
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
    this.idAdmin = admin.id_administrador; // 👈 GUARDAMOS EL ID
  }

  cerrarSesion(): void {
    localStorage.removeItem('administrador');
    this.router.navigate(['/login-admin']);
  }
}