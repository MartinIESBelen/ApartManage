import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  // Aquí guardaremos lo que el usuario escriba en las cajas de texto
  credentials: LoginRequest = {
    email: '',
    password: '',
  };

  // Por si el usuario se equivoca de contraseña
  errorMessage: string = '';


  private authService = inject(AuthService);
  private router = inject(Router);


  onSubmit() {
    this.authService.login(this.credentials).subscribe({
      next: (response) => {

        this.authService.guardarToken(response.token);

        this.errorMessage = '';

        console.log("¡Login Exitoso! Token guardado:", response.token);

        this.router.navigate(['/home']);
      },
      error: (err) => {
        // Si el backend devuelve un error (ej: 403 Forbidden)
        this.errorMessage = 'Credenciales incorrectas. Inténtalo de nuevo.';
        console.error("Error en el login:", err);
      }
    });
  }
}
