import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

// Banner principal (Hero) de la página de inicio con acceso directo al menú
@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [RouterLink], // Importamos RouterLink para el botón del menú
  templateUrl: './hero.component.html',
  styleUrl: './hero.component.scss'
})
export class HeroComponent { 
}
