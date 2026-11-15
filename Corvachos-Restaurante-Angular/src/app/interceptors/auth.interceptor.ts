import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { Injector, inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../service/auth.service';
import { leerPayload, obtenerTokenValido } from '../service/token.util';
import { Rol } from '../models/auth.model';

// Agrega "Authorization: Bearer <token>" a todas las peticiones al backend.
// Si el backend responde 401 (token vencido o inválido) se cierra la sesión y se manda al login
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // AuthService se pide solo cuando se necesita (evita una dependencia circular con CarritoService)
  const injector = inject(Injector);

  const token = obtenerTokenValido();
  const esLogin = req.url.endsWith('/login');
  const peticion = token && !esLogin
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !esLogin) {
        const authService = injector.get(AuthService);
        const rolGuardado = localStorage.getItem('rol') ?? (token ? leerPayload(token)?.rol : undefined);
        authService.cerrarSesion();
        injector.get(Router).navigate([authService.rutaLogin(rolGuardado as Rol | undefined)]);
      }
      return throwError(() => error);
    })
  );
};
