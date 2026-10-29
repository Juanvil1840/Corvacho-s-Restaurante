import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AdministradorService } from '../../service/administrador.service';
import { Administrador } from '../../models/administrador.model';
import { AdministradorCamposComponent } from './components/administrador-campos/administrador-campos.component';

// Página de formulario para registrar o editar un administrador
@Component({
  selector: 'app-formulario-administrador',
  standalone: true,
  imports: [CommonModule, FormsModule, AdministradorCamposComponent],
  templateUrl: './formulario-administrador.component.html',
  styleUrl: './formulario-administrador.component.scss'
})
export class FormularioAdministradorComponent implements OnInit {

  // Datos del administrador vinculados al formulario
  administrador: Administrador = {
    id_administrador: 0,
    nombre: '',
    usuario: '',
    contrasena: ''
  };

  // Variables de estado de edición, guardado y mensajes de error
  editando = false;
  guardando = false;
  error = '';

  constructor(
    private administradorService: AdministradorService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  // Si existe un ID en la ruta, activa el modo edición y consulta los datos del administrador
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.editando = true;

      this.administradorService.getAdministradorById(Number(id)).subscribe({
        next: (administrador) => {
          this.administrador = { ...administrador };
        },
        error: (error) => {
          console.error('Error al cargar el administrador:', error);
          this.error = 'No fue posible cargar el administrador.';
        }
      });
    }
  }

  // Valida los campos obligatorios y guarda o actualiza el administrador en el backend
  guardar(): void {
    // Validación de campos obligatorios (la contraseña solo es obligatoria al crear)
    if (!this.administrador.nombre.trim() || !this.administrador.usuario.trim()
        || (!this.editando && !this.administrador.contrasena?.trim())) {
      this.error = 'Por favor completa todos los campos obligatorios.';
      return;
    }

    this.guardando = true;
    this.error = '';

    const peticion = this.editando
      ? this.administradorService.actualizarAdministrador(this.administrador)
      : this.administradorService.agregarAdministrador(this.administrador);

    peticion.subscribe({
      next: (adminGuardado) => {
        this.guardando = false;

        const adminLogueadoStr = localStorage.getItem('administrador');
        
        if (adminLogueadoStr) {
          const adminLogueado = JSON.parse(adminLogueadoStr);
          
          // Si el ID del que editamos coincide con el del logueado
          if (adminLogueado.id_administrador === adminGuardado.id_administrador) {
            // 1. Actualizamos el localStorage con el nombre nuevo
            localStorage.setItem('administrador', JSON.stringify(adminGuardado));
            // 2. Lo enviamos de vuelta a su portal
            this.router.navigate(['/portal-admin']);
            return; // Terminamos aquí
          }
        }

        // Si editamos a otro admin (desde la lista general), volvemos a la lista
        this.router.navigate(['/administradores']);
      },
      error: (error) => {
        console.error('Error al guardar el administrador:', error);
        this.guardando = false;
        this.error = error?.error?.message || 'No fue posible guardar el administrador. Intenta nuevamente.';
      }
    });
  }

  // Cancela la edición y regresa al portal o a la lista de administradores según corresponda
  cancelar(): void {
    // Si cancela, se devuelve a la pantalla de donde vino
    const adminLogueadoStr = localStorage.getItem('administrador');
    if (adminLogueadoStr) {
      const adminLogueado = JSON.parse(adminLogueadoStr);
      // Si estábamos editando a nuestro propio perfil, volvemos al portal
      if (adminLogueado.id_administrador === this.administrador.id_administrador) {
        this.router.navigate(['/portal-admin']);
        return;
      }
    }
    // Si no, volvemos a la lista
    this.router.navigate(['/administradores']);
  }
}
