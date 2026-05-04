import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.obtenerToken();

  // 1. Clonamos la petición si hay token y no es login/register
  let requestToForward = req;
  if (token && !req.url.endsWith('/login') && !req.url.endsWith('/register')) {
    requestToForward = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}` // Asegúrate de que el token sea correcto
      }
    });
  }

  // 2. Enviamos la petición y estamos atentos por si el backend nos echa (401 o 403)
  return next(requestToForward).pipe(
    catchError((error: HttpErrorResponse) => {
      // ¡AQUÍ ESTÁ EL CAMBIO! Atrapamos el 401 y el temido 403
      if (error.status === 401 || error.status === 403) {
        console.warn('Token caducado, inválido o sin permisos. Redirigiendo al login...');

        authService.logout();

        // Expulsamos al usuario a la pantalla de login
        router.navigate(['/login']);
      }

      // Dejamos que el error siga su camino por si el componente quiere hacer algo más
      return throwError(() => error);
    })
  );
};
