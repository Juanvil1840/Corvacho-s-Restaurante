import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Producto } from '../../../../models/producto.model';

@Component({
  selector: 'app-producto-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './producto-card.component.html',
  styleUrl: './producto-card.component.scss'
})
export class ProductoCardComponent {

  @Input() producto!: Producto;

}