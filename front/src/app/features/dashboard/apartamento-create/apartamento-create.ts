import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApartamentoService } from '../../../core/services/apartamento.service';

@Component({
  selector: 'app-apartamento-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './apartamento-create.html'
})
export class ApartamentoCreate {

  private fb = inject(FormBuilder);
  private apartamentoService = inject(ApartamentoService);
  private router = inject(Router);

  guardando = false;
  mensajeError = '';

  // Formulario en blanco
  formApartamento: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    direccion: ['', Validators.required],
    ciudad: ['', Validators.required],
    descripcion: ['']
  });

  onSubmit() {
    if (this.formApartamento.invalid) {
      this.formApartamento.markAllAsTouched();
      return;
    }

    this.guardando = true;

    // Llamamos al POST
    this.apartamentoService.crearApartamento(this.formApartamento.value).subscribe({
      next: (nuevoApto) => {
        this.guardando = false;
        // Si va bien, viajamos directamente a ver los detalles del piso recién creado
        this.router.navigate(['/apartamento', nuevoApto.id]);
      },
      error: (err) => {
        this.mensajeError = 'Error al crear el apartamento.';
        this.guardando = false;
      }
    });
  }
}
