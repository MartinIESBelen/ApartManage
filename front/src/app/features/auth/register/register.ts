import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth/auth.service';
import { RegisterRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink], // Añadimos RouterLink para navegar
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {

  // Inicializamos el objeto con INQUILINO por defecto
  userData: RegisterRequest = {
    nombreCompleto: '',
    email: '',
    password: '',
    rol: 'INQUILINO'
  };

  errorMessage: string = '';

  private authService = inject(AuthService);
  private router = inject(Router);

  onSubmit() {
    this.authService.register(this.userData).subscribe({
      next: (response) => {
        // Guardamos el token que nos devuelve Spring Boot
        this.authService.guardarToken(response.token);
        this.errorMessage = '';

        console.log("¡Registro Exitoso! Entrando a la app...");

        // Redirigimos al home directamente
        this.router.navigate(['/home']);
      },
      error: (err) => {
        // Capturamos el mensaje de error del backend (ej: "El email ya está registrado")
        this.errorMessage = err.error?.message || 'Error al crear la cuenta. Revisa los datos.';
        console.error("Error en el registro:", err);
      }
    });
  }
}
