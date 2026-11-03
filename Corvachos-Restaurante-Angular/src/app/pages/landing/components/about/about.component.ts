import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// Sección informativa "Sobre nosotros" en la página de inicio del restaurante
@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss']
})
export class AboutComponent {}
