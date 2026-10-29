import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { OperadorService } from '../../service/operador.service';
import { Operador } from '../../models/operador.model';
import { OperadorCamposComponent } from './components/operador-campos/operador-campos.component';

// Página de formulario para registrar o editar un operador del sistema
@Component({
  selector: 'app-formulario-operador',
  standalone: true,
  imports: [CommonModule, FormsModule, OperadorCamposComponent],
  templateUrl: './formulario-operador.component.html',
  styleUrl: './formulario-operador.component.scss'
})
export class FormularioOperadorComponent implements OnInit {

  // Datos del operador vinculados al formulario
  operador: Operador = {
    id_operador: 0,
    nombre: '',
    usuario: '',
    contrasena: ''
  };

  // Estados de modo edición, guardado en curso y mensaje de error
  editando = false;
  guardando = false;
  error = '';

  constructor(
    private operadorService: OperadorService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  // Si la ruta incluye un ID, activa la edición y consulta los datos del operador
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.editando = true;

      this.operadorService.getOperadorById(Number(id)).subscribe({
        next: (operador) => {
          this.operador = { ...operador };
        },
        error: (error) => {
          console.error('Error al cargar el operador:', error);
          this.error = 'No fue posible cargar el operador.';
        }
      });
    }
  }

  // Valida los campos obligatorios y guarda o actualiza el operador en el backend
  guardar(): void {
    // Verifica que nombre, usuario y contraseña (al crear) no estén vacíos
    if (!this.operador.nombre.trim() || !this.operador.usuario.trim() || (!this.editando && !this.operador.contrasena?.trim())) {
      this.error = 'Por favor completa todos los campos obligatorios.';
      return;
    }

    this.guardando = true;
    this.error = '';

    const peticion = this.editando
      ? this.operadorService.actualizarOperador(this.operador)
      : this.operadorService.agregarOperador(this.operador);

    peticion.subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/operadores']);
      },
      error: (error) => {
        console.error('Error al guardar el operador:', error);
        this.guardando = false;
        this.error = error?.error?.message || 'No fue posible guardar el operador. Verifica que el usuario no esté repetido.';
      }
    });
  }

  // Cancela la operación y regresa al listado de operadores
  cancelar(): void {
    this.router.navigate(['/operadores']);
  }
}
