import { Injectable } from '@angular/core';
import confetti from 'canvas-confetti';

@Injectable({
  providedIn: 'root',
})
export class ConfetiService {
  lanzarCelebracion(): void {
    // Ráfaga principal
    confetti({
      particleCount: 90,
      spread: 75,
      origin: { y: 0.6 },
    });

    // Segunda ráfaga suave desde los lados para que se vea más vistoso
    setTimeout(() => {
      confetti({
        particleCount: 45,
        angle: 60,
        spread: 55,
        origin: { x: 0.1, y: 0.65 },
      });
      confetti({
        particleCount: 45,
        angle: 120,
        spread: 55,
        origin: { x: 0.9, y: 0.65 },
      });
    }, 180);
  }
}