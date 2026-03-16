import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // <-- Vital para usar formularios
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule], // <-- Activamos las herramientas visuales
  templateUrl: './login.html',
  styleUrl: './login.css' // o .scss si elegiste ese formato
})
export class LoginComponent {
  // Aquí guardaremos lo que el usuario escriba en las cajas de texto
  credentials: LoginRequest = {
    email: '',
    password: '',
  };

  // Por si el usuario se equivoca de contraseña
  errorMessage: string = '';

  // Inyectamos nuestras herramientas
  private authService = inject(AuthService);
  private router = inject(Router);

  // Esta función se ejecuta al pulsar el botón "Entrar"
  onSubmit() {
    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        // Si el backend dice "OK", guardamos el Token
        this.authService.guardarToken(response.token);

        // Limpiamos errores
        this.errorMessage = '';

        console.log("¡Login Exitoso! Token guardado:", response.token);

        // AÑADIMOS ESTA LÍNEA PARA VIAJAR AL HOME
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
