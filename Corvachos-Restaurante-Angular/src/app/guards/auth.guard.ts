import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { Rol } from '../models/auth.model';

// Protege una ruta según el rol. Se usa en app.routes.ts así:
//   { path: 'portal-admin', component: ..., canActivate: [authGuard], data: { roles: ['ADMIN'] } }
// - Sin sesión: manda al login que corresponde
// - Con sesión pero otro rol: manda a la página de inicio de su rol
export const authGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const roles = (route.data?.['roles'] ?? []) as Rol[];

  if (!authService.estaAutenticado()) {
    return router.parseUrl(authService.rutaLogin(roles[0]));
  }
  if (roles.length > 0 && !authService.tieneRol(roles)) {
    return router.parseUrl(authService.rutaInicio());
  }
  return true;
};

// Para el home y las páginas de login/registro: si ya hay un JWT válido
// se redirige a la página de inicio del rol en vez de mostrar la página
export const sinSesionGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.estaAutenticado()) {
    return router.parseUrl(authService.rutaInicio());
  }
  return true;
};
