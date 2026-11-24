import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfetiService } from '../../../../service/confeti.service';

export interface SegmentoRuleta {
  etiqueta: string;
  tipo: 'descuento' | 'frase';
  porcentaje?: number;
  mensaje: string;
}

@Component({
  selector: 'app-ruleta-bienvenida',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ruleta-bienvenida.component.html',
  styleUrl: './ruleta-bienvenida.component.scss'
})
export class RuletaBienvenidaComponent implements OnInit {

  // Correo del usuario recién registrado para verificar que no reclame el giro más de una vez
  @Input() correoUsuario = '';
  @Output() continuar = new EventEmitter<void>();

  // 1 de 10%, 1 de 5% y 4 frases de Corvacho's (6 porciones en total)
  readonly segmentos: SegmentoRuleta[] = [
    {
      etiqueta: '10% OFF',
      tipo: 'descuento',
      porcentaje: 10,
      mensaje: '¡Ganaste un 10% de descuento en tu pedido en Corvacho\'s!'
    },
    {
      etiqueta: 'En Corvacho\'s la suerte siempre de tu lado',
      tipo: 'frase',
      mensaje: 'En Corvacho\'s la suerte siempre de tu lado. ¡Disfruta tu experiencia!'
    },
    {
      etiqueta: 'Corvacho\'s, la suerte de tenerte en casa',
      tipo: 'frase',
      mensaje: 'Corvacho\'s, la suerte de tenerte en casa. ¡Bienvenido a la familia!'
    },
    {
      etiqueta: '5% OFF',
      tipo: 'descuento',
      porcentaje: 5,
      mensaje: '¡Ganaste un 5% de descuento en tu pedido en Corvacho\'s!'
    },
    {
      etiqueta: 'Cada bocado en Corvacho\'s es buena suerte',
      tipo: 'frase',
      mensaje: 'Cada bocado en Corvacho\'s es buena suerte. ¡Buen provecho!'
    },
    {
      etiqueta: 'Tu mesa te espera en Corvacho\'s',
      tipo: 'frase',
      mensaje: 'Tu mesa te espera en Corvacho\'s. ¡Gracias por elegirnos!'
    }
  ];

  girando = false;
  yaGiro = false;
  mensajeEstado = '';
  resultado: SegmentoRuleta | null = null;
  rotacionActual = 0;

  private readonly claveGiros = 'corvachos_ruleta_correos';
  private readonly claveDescuento = 'corvachos_descuento_bienvenida';

  constructor(private confetiService: ConfetiService) {}

  ngOnInit(): void {
    const correoLimpio = this.normalizarCorreo(this.correoUsuario);
    if (correoLimpio && this.correoYaParticipo(correoLimpio)) {
      this.yaGiro = true;
      this.mensajeEstado = 'Este correo ya participó en la ruleta de bienvenida.';
    }
  }

  // Gira la ruleta si el correo no ha participado antes y aplica el premio al terminar
  girar(): void {
    if (this.girando || this.yaGiro) {
      return;
    }

    const correoLimpio = this.normalizarCorreo(this.correoUsuario);
    if (!correoLimpio) {
      this.mensajeEstado = 'No se pudo validar el correo del usuario.';
      return;
    }

    if (this.correoYaParticipo(correoLimpio)) {
      this.yaGiro = true;
      this.mensajeEstado = 'Este correo ya reclamó su giro de bienvenida.';
      return;
    }

    this.girando = true;
    this.mensajeEstado = '';
    this.resultado = null;

    const totalSegmentos = this.segmentos.length;
    const anguloSegmento = 360 / totalSegmentos;
    const indiceGanador = Math.floor(Math.random() * totalSegmentos);
    const segmentoElegido = this.segmentos[indiceGanador];

    // Calcula el ángulo para que el puntero superior apunte al centro del segmento elegido
    const centroSegmento = indiceGanador * anguloSegmento + anguloSegmento / 2;
    const vueltasExtra = 5 * 360;
    const objetivo = vueltasExtra + (360 - centroSegmento);
    this.rotacionActual = this.rotacionActual + objetivo - (this.rotacionActual % 360);

    setTimeout(() => {
      this.girando = false;
      this.yaGiro = true;
      this.resultado = segmentoElegido;
      this.marcarCorreoParticipado(correoLimpio);

      if (segmentoElegido.tipo === 'descuento' && segmentoElegido.porcentaje) {
        localStorage.setItem(
          this.claveDescuento,
          JSON.stringify({
            porcentaje: segmentoElegido.porcentaje,
            correo: correoLimpio,
            usado: false
          })
        );
        this.confetiService.lanzarCelebracion();
      }
    }, 3200);
  }

  irAlPerfil(): void {
    this.continuar.emit();
  }

  private normalizarCorreo(correo: string): string {
    return (correo || '').trim().toLowerCase();
  }

  private obtenerCorreosParticipantes(): string[] {
    try {
      const raw = localStorage.getItem(this.claveGiros);
      return raw ? JSON.parse(raw) : [];
    } catch {
      return [];
    }
  }

  private correoYaParticipo(correo: string): boolean {
    return this.obtenerCorreosParticipantes().includes(correo);
  }

  private marcarCorreoParticipado(correo: string): void {
    const lista = this.obtenerCorreosParticipantes();
    if (!lista.includes(correo)) {
      lista.push(correo);
      localStorage.setItem(this.claveGiros, JSON.stringify(lista));
    }
  }
}