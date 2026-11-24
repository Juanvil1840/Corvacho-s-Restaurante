import { Component, Input } from '@angular/core';

interface Celda {
  hora: number;
  valor: number;
  intensidad: number;
}

// Mapa de calor dia de la semana x hora: muestra en que momentos llegan mas pedidos
@Component({
  selector: 'app-mapa-calor',
  standalone: true,
  templateUrl: './mapa-calor.component.html',
  styleUrl: './mapa-calor.component.scss'
})
export class MapaCalorComponent {

  // Muestras de la escala para la leyenda (de pocos a muchos pedidos)
  readonly pasosLeyenda = [0.1, 0.3, 0.5, 0.75, 1];

  readonly dias = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];

  horas: number[] = [];
  filas: Celda[][] = [];
  pico: { dia: string; hora: number; valor: number } | null = null;

  // Recibe la matriz 7x24 del backend y recorta las horas en las que nunca hubo pedidos
  @Input({ required: true }) set matriz(matriz: number[][]) {
    const porHora = Array.from({ length: 24 }, (_, h) => matriz.reduce((s, fila) => s + (fila[h] ?? 0), 0));
    const conPedidos = porHora.map((v, h) => (v > 0 ? h : -1)).filter(h => h >= 0);
    const primera = conPedidos.length ? conPedidos[0] : 11;
    const ultima = conPedidos.length ? conPedidos[conPedidos.length - 1] : 22;
    this.horas = Array.from({ length: ultima - primera + 1 }, (_, i) => primera + i);

    const maximo = Math.max(1, ...matriz.flat());
    this.pico = null;
    this.filas = matriz.map((fila, d) => this.horas.map(hora => {
      const valor = fila[hora] ?? 0;
      if (valor > 0 && (!this.pico || valor > this.pico.valor)) {
        this.pico = { dia: this.dias[d], hora, valor };
      }
      return { hora, valor, intensidad: valor / maximo };
    }));
  }

  // Escala secuencial de un solo tono (dorado): mas pedidos = celda mas intensa
  color(intensidad: number): string {
    if (intensidad === 0) {
      return 'rgba(255, 255, 255, 0.03)';
    }
    return `rgba(205, 158, 70, ${(0.12 + intensidad * 0.88).toFixed(2)})`;
  }

  formatoHora(hora: number): string {
    const sufijo = hora < 12 ? 'am' : 'pm';
    const h = hora % 12 === 0 ? 12 : hora % 12;
    return `${h}${sufijo}`;
  }

  descripcion(dia: string, celda: Celda): string {
    const texto = celda.valor === 1 ? 'pedido' : 'pedidos';
    return `${dia} ${this.formatoHora(celda.hora)}: ${celda.valor} ${texto}`;
  }
}
