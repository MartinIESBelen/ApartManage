import { Component, inject } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth/auth.service'; // Ajusta la ruta si es necesario

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './header.html'
})
export class HeaderComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  // Comprueba si el usuario tiene un token activo
  isLoggedIn(): boolean {
    return this.authService.obtenerToken() !== null;
  }

  // Método para salir
  logout() {
    // Asegúrate de tener este método en tu AuthService que haga: localStorage.removeItem('jwt_token');
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
