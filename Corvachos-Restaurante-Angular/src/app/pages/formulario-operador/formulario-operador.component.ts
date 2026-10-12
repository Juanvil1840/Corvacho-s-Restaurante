import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { OperadorService } from '../../service/operador.service';
import { Operador } from '../../models/operador.model';
import { OperadorCamposComponent } from './components/operador-campos/operador-campos.component';

@Component({
  selector: 'app-formulario-operador',
  standalone: true,
  imports: [CommonModule, FormsModule, OperadorCamposComponent],
  templateUrl: './formulario-operador.component.html',
  styleUrl: './formulario-operador.component.scss'
})
export class FormularioOperadorComponent implements OnInit {

  operador: Operador = {
    id_operador: 0,
    nombre: '',
    usuario: '',
    contrasena: ''
  };

  editando = false;
  guardando = false;
  error = '';

  constructor(
    private operadorService: OperadorService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

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

  guardar(): void {
    if (!this.operador.nombre.trim() || !this.operador.usuario.trim() || (!this.editando && !this.operador.contrasena.trim())) {
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

  cancelar(): void {
    this.router.navigate(['/operadores']);
  }
}

