import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withInMemoryScrolling } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { routes } from './app.routes';

// Configuración global de la aplicación Angular (enrutamiento, cliente HTTP y detección de cambios)
export const appConfig: ApplicationConfig = {
  providers: [
    // Optimiza la detección de cambios agrupando eventos
    provideZoneChangeDetection({ eventCoalescing: true }), 
    // Configura las rutas de la aplicación y restaura el scroll al inicio al navegar
    provideRouter(
      routes, 
      withInMemoryScrolling({ scrollPositionRestoration: 'top' })
    ), 
    // Habilita el cliente HTTP para comunicarse con el backend en Spring Boot
    provideHttpClient()
  ],
};
