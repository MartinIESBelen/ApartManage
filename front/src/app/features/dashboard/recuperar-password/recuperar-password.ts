import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth/auth.service';

@Component({
  selector: 'app-recuperar-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './recuperar-password.html'
})
export class RecuperarPassword {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  formRecuperar: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  enviando = false;
  mensajeExito = '';
  mensajeError = '';

  onSubmit() {
    if (this.formRecuperar.invalid) {
      this.formRecuperar.markAllAsTouched();
      return;
    }

    this.enviando = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    const email = this.formRecuperar.get('email')?.value;

    this.authService.solicitarRecuperacion(email).subscribe({
      next: (respuesta) => {
        this.enviando = false;
        // Mostramos el mensaje que nos devuelve el backend
        this.mensajeExito = respuesta || 'Si el correo existe, recibirás instrucciones en breve.';
        this.formRecuperar.reset();
      },
      error: (err) => {
        this.enviando = false;
        this.mensajeError = 'Hubo un error al intentar enviar el correo. Inténtalo más tarde.';
        console.error(err);
      }
    });
  }
}
