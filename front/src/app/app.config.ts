import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';

// Importamos el cliente HTTP y la herramienta de interceptores
import { provideHttpClient, withInterceptors } from '@angular/common/http';

// Importamos tu interceptor (ajusta la ruta y nombre según lo creaste)
import { authInterceptor } from './core/interceptors/auth-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(), // <-- Tu configuración original
    provideRouter(routes),                // <-- Tus rutas

    // 👇 Solo modificamos esta línea para añadir el interceptor
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};
