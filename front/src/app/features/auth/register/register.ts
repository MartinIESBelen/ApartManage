import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router} from '@angular/router';
import { AuthService } from '../../../core/services/auth/auth.service';
import { RegisterRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  errorMessage: string = '';

  // 1. Declaración del Formulario Reactivo
  registerForm: FormGroup = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.minLength(2), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
    apellidos: ['', [Validators.required, Validators.minLength(2), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
    dniPasaporte: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9]{5,20}$/)]],
    fechaNacimiento: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9._%\+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,4}$/)]],
    password: ['', [Validators.required, Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{6,}$/)]],
    passwordConfirm: ['', [Validators.required]],
    rol: ['INQUILINO']
  }, {
    validators: this.passwordsMatchValidator
  });

  // 2. Validador personalizado para las contraseñas
  private passwordsMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const passwordConfirm = control.get('passwordConfirm')?.value;
    return password === passwordConfirm ? null : { passwordsMismatch: true };
  }

  // 3. Getters rápidos para el HTML
  get nombreControl() { return this.registerForm.get('nombre'); }
  get apellidosControl() { return this.registerForm.get('apellidos'); }
  get dniControl() { return this.registerForm.get('dniPasaporte'); }
  get fechaControl() { return this.registerForm.get('fechaNacimiento'); }
  get emailControl() { return this.registerForm.get('email'); }
  get passwordControl() { return this.registerForm.get('password'); }
  get passwordConfirmControl() { return this.registerForm.get('passwordConfirm'); }

  // 4. Envío de datos
  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched(); // Muestra todos los errores si el usuario le da a enviar pronto
      return;
    }

    // Extraemos los valores y los convertimos al modelo que espera tu backend
    const formValues = this.registerForm.getRawValue();
    const requestData: RegisterRequest = {
      nombre: formValues.nombre,
      apellidos: formValues.apellidos,
      email: formValues.email,
      password: formValues.password,
      dniPasaporte: formValues.dniPasaporte,
      fechaNacimiento: formValues.fechaNacimiento,
      rol: formValues.rol
    };

    this.authService.register(requestData).subscribe({
      next: (response) => {
        this.authService.guardarToken(response.accessToken);
        this.errorMessage = '';
        void this.router.navigate(['/home']);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Error al crear la cuenta. Revisa los datos.';
      }
    });
  }
}
