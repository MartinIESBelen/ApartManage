import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './reset-password.html'
})
export class ResetPassword implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute); // Para leer el ?token=

  token: string | null = null;
  enviando = false;
  mensajeExito = '';
  mensajeError = '';

  formReset: FormGroup = this.fb.group({
    nuevaPassword: ['', [Validators.required, Validators.minLength(6)]],
    confirmarPassword: ['', Validators.required]
  }, { validators: this.passwordsCoinciden });

  ngOnInit() {
    // Extraemos el token de la URL
    this.token = this.route.snapshot.queryParamMap.get('token');
    if (!this.token) {
      this.mensajeError = 'Enlace inválido. No se ha proporcionado un token de seguridad.';
    }
  }

  // Validador personalizado para asegurar que ambas contraseñas son iguales
  passwordsCoinciden(group: AbstractControl): ValidationErrors | null {
    const pass = group.get('nuevaPassword')?.value;
    const confirm = group.get('confirmarPassword')?.value;
    return pass === confirm ? null : { noCoinciden: true };
  }

  onSubmit() {
    if (this.formReset.invalid || !this.token) {
      this.formReset.markAllAsTouched();
      return;
    }

    this.enviando = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    const nuevaPassword = this.formReset.get('nuevaPassword')?.value;

    this.authService.resetearPassword(this.token, nuevaPassword).subscribe({
      next: (respuesta) => {
        this.enviando = false;
        this.mensajeExito = respuesta || 'Contraseña actualizada correctamente.';
        this.formReset.reset();
      },
      error: (err) => {
        this.enviando = false;
        // Si el backend lanza el RuntimeException (token caducado/inválido), lo mostramos
        this.mensajeError = err.error || 'El enlace es inválido o ha caducado.';
        console.error(err);
      }
    });
  }
}
