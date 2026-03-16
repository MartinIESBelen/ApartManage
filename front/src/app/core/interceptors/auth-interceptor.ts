import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service'; // <-- Ajusta la ruta a tu auth.service.ts

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Inyectamos el servicio de autenticación para sacar el token
  const authService = inject(AuthService);
  const token = authService.obtenerToken();

  // Si tenemos un token guardado, clonamos la petición original y le pegamos la cabecera
  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    // Enviamos la petición modificada al backend
    return next(clonedRequest);
  }

  // Si no hay token (ej: el usuario no ha hecho login), enviamos la petición tal cual
  return next(req);
};
