import { Component, inject, computed } from '@angular/core';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './header.html'
})
export class HeaderComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  menuMobileAbierto: boolean = false;


  menuFinanzasAbierto: boolean = false;

  toggleMenuFinanzas() {
    this.menuFinanzasAbierto = !this.menuFinanzasAbierto;
  }

  abrirMenuFinanzas() {
    this.menuFinanzasAbierto = true;
  }

  cerrarMenuFinanzas() {
    this.menuFinanzasAbierto = false;
  }

  toggleMenuMobile() {
    this.menuMobileAbierto = !this.menuMobileAbierto;
  }

  cerrarMenuMobile() {
    this.menuMobileAbierto = false;
  }

  estaLogueado = computed(() => this.authService.obtenerToken() !== null);

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
