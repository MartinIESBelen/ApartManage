import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ApartamentoService } from '../../../core/services/apartamento/apartamento.service';
import { switchMap, of, map } from 'rxjs';
import {ApartamentoModel} from '../../../core/models/apartamento.model';

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

  archivoSeleccionado: File | null = null;
  imagenPreview: string | null = null;

  formApartamento: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    direccion: ['', Validators.required],
    ciudad: ['', Validators.required],
    descripcion: ['']
  });

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.archivoSeleccionado = file;

      // Magia para crear la vista previa en pantalla
      const reader = new FileReader();
      reader.onload = e => this.imagenPreview = reader.result as string;
      reader.readAsDataURL(file);
    }
  }

  onSubmit() {
    if (this.formApartamento.invalid) {
      this.formApartamento.markAllAsTouched();
      return;
    }

    this.guardando = true;

    this.apartamentoService.crearApartamento(this.formApartamento.value).pipe(
      switchMap((nuevoApto: ApartamentoModel) => {
        if (this.archivoSeleccionado) {
          return this.apartamentoService.subirImagen(nuevoApto.id, this.archivoSeleccionado).pipe(
            map(() => nuevoApto)
          );
        }
        return of(nuevoApto);
      })
    ).subscribe({
      next: (nuevoApto) => {
        this.guardando = false;
        this.router.navigate(['/apartamento', nuevoApto.id]);
      },
      error: (err) => {
        this.mensajeError = err.message?.includes('imagen')
          ? 'La vivienda se creó, pero hubo un error al subir la foto.'
          : 'Error al crear el apartamento.';
        this.guardando = false;
      }
    });
  }
}
