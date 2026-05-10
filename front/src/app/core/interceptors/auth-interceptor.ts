import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.obtenerToken();

  let requestToForward = req;
  if (token && !req.url.endsWith('/login') && !req.url.endsWith('/register')) {
    requestToForward = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  //  Enviamos la petición y estamos atentos por si el backend nos echa (401 o 403)
  return next(requestToForward).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
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
