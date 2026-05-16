import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterLink} from '@angular/router';
import { AuthService } from '../../../core/services/auth/auth.service';
import { RegisterRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  errorMessage: string = '';

  registerForm: FormGroup = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.minLength(2), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
    apellidos: ['', [Validators.required, Validators.minLength(2), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
    dniPasaporte: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9]{5,20}$/)]],
    fechaNacimiento: ['', [Validators.required, this.mayorDeEdadValidator.bind(this)]],
    email: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9._%\+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,4}$/)]],
    password: ['', [Validators.required, Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{6,}$/)]],
    passwordConfirm: ['', [Validators.required]],
    rol: ['INQUILINO']
  }, {
    validators: this.passwordsMatchValidator
  });

  private passwordsMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const passwordConfirm = control.get('passwordConfirm')?.value;
    return password === passwordConfirm ? null : { passwordsMismatch: true };
  }

  get nombreControl() { return this.registerForm.get('nombre'); }
  get apellidosControl() { return this.registerForm.get('apellidos'); }
  get dniControl() { return this.registerForm.get('dniPasaporte'); }
  get fechaControl() { return this.registerForm.get('fechaNacimiento'); }
  get emailControl() { return this.registerForm.get('email'); }
  get passwordControl() { return this.registerForm.get('password'); }
  get passwordConfirmControl() { return this.registerForm.get('passwordConfirm'); }

  private mayorDeEdadValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;

    const fechaNacimiento = new Date(control.value);
    const hoy = new Date();

    let edad = hoy.getFullYear() - fechaNacimiento.getFullYear();
    const mes = hoy.getMonth() - fechaNacimiento.getMonth();

    if (mes < 0 || (mes === 0 && hoy.getDate() < fechaNacimiento.getDate())) {
      edad--;
    }

    return edad >= 18 ? null : { esMenor: true };
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

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
        const errorDelServidor = err.error?.message || err.error || '';

        if (typeof errorDelServidor === 'string' && errorDelServidor.toLowerCase().includes('email')) {
          this.errorMessage = 'Ya existe una cuenta con este correo electrónico. Por favor, inicia sesión.';
        } else {
          this.errorMessage = 'Error al crear la cuenta. Revisa los datos y vuelve a intentarlo.';
        }
      }
    });
  }
}
