import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DomiciliarioService } from '../../service/domiciliario.service';
import { OperadorService } from '../../service/operador.service';
import { Domiciliario } from '../../models/domiciliario.model';
import { Operador } from '../../models/operador.model';
import { DomiciliarioCamposComponent } from './components/domiciliario-campos/domiciliario-campos.component';

// Página de formulario para registrar o editar un domiciliario y su operador encargado
@Component({
  selector: 'app-formulario-domiciliario',
  standalone: true,
  imports: [CommonModule, FormsModule, DomiciliarioCamposComponent],
  templateUrl: './formulario-domiciliario.component.html',
  styleUrl: './formulario-domiciliario.component.scss'
})
export class FormularioDomiciliarioComponent implements OnInit {

  // Datos del domiciliario vinculados al formulario
  domiciliario: Domiciliario = {
    id_domiciliario: 0,
    nombre: '',
    celular: '',
    cedula: '',
    informaciondisp: true,
    activo: true,
    operador: { id_operador: 0, nombre: '', usuario: '', contrasena: '' }
  };

  // Lista de operadores disponibles y estados del formulario
  operadores: Operador[] = [];
  editando = false;
  guardando = false;
  error = '';

  constructor(
    private domiciliarioService: DomiciliarioService,
    private operadorService: OperadorService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  // Carga la lista de operadores y, si viene un ID en la URL, consulta los datos del domiciliario
  ngOnInit(): void {
    this.operadorService.getOperadores().subscribe({
      next: (ops) => this.operadores = ops,
      error: (e) => console.error('Error al cargar operadores:', e)
    });

    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.editando = true;

      this.domiciliarioService.getDomiciliarioById(Number(id)).subscribe({
        next: (domiciliario) => {
          this.domiciliario = {
            ...domiciliario,
            operador: domiciliario.operador ?? {
              id_operador: 0,
              nombre: '',
              usuario: '',
              contrasena: ''
            }
          };
        },
        error: (error) => {
          console.error('Error al cargar el domiciliario:', error);
          this.error = 'No fue posible cargar el domiciliario.';
        }
      });
    }
  }

  // Asigna el objeto completo del operador según el ID seleccionado en el formulario
  actualizarOperador(): void {
    if (!this.domiciliario.operador) {
      return;
    }
    const operadorId = Number(this.domiciliario.operador.id_operador);
    const operadorSeleccionado = this.operadores.find(
      op => op.id_operador === operadorId
    );

    if (operadorSeleccionado) {
      this.domiciliario.operador = operadorSeleccionado;
    }
  }

  // Envía la petición para crear o actualizar el domiciliario y navega al listado
  guardar(): void {
    this.actualizarOperador();
    this.guardando = true;
    this.error = '';

    const peticion = this.editando
      ? this.domiciliarioService.actualizarDomiciliario(this.domiciliario)
      : this.domiciliarioService.agregarDomiciliario(this.domiciliario);

    peticion.subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/domiciliarios']);
      },
      error: (error) => {
        console.error('Error al guardar el domiciliario:', error);
        this.guardando = false;
        this.error = 'No fue posible guardar el domiciliario. Intenta nuevamente.';
      }
    });
  }

  // Cancela la acción y vuelve a la lista de domiciliarios
  cancelar(): void {
    this.router.navigate(['/domiciliarios']);
  }
}
