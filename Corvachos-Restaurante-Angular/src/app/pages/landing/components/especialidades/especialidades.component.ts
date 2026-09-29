import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-especialidades',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './especialidades.component.html',
  styleUrls: ['./especialidades.component.scss']
})
export class EspecialidadesComponent {
  platos = [
    { nombre: 'Berenjenas a la parmesana', imagen: '/Images/comida-berenjenas_a_la_parmesana.png' },
    { nombre: 'Burrata', imagen: '/Images/Comida-burrata.png' },
    { nombre: 'Carpaccio di salmone', imagen: '/Images/comida-carpaccio_di_salmone.png' },
    { nombre: 'Papas a la Francesa', imagen: '/Images/comida-papas_a_la_francesa.png' },
    { nombre: 'Pasta gratinada del chef', imagen: '/Images/comida-pasta_gratinada_del_chef.png' },
    { nombre: 'Polpo alla griglia', imagen: '/Images/comida-polpo_alla_griglia.png' }
  ];
}