import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TemaService {

  constructor() {
    // Al iniciar, revisamos si el usuario ya había elegido un tema
    const temaGuardado = localStorage.getItem('tema');
    if (temaGuardado === 'claro') {
      this.activarModoClaro();
    } else {
      this.activarModoOscuro();
    }
  }

  // Alterna entre modo claro y oscuro
  toggleTema(): void {
    const esClaro = document.body.classList.contains('modo-claro');
    if (esClaro) {
      this.activarModoOscuro();
    } else {
      this.activarModoClaro();
    }
  }

  private activarModoClaro(): void {
    document.body.classList.add('modo-claro');
    localStorage.setItem('tema', 'claro');
  }

  private activarModoOscuro(): void {
    document.body.classList.remove('modo-claro');
    localStorage.setItem('tema', 'oscuro');
  }
}