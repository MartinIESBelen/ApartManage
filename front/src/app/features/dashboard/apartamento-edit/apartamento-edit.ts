import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ApartamentoService } from '../../../core/services/apartamento/apartamento.service';
import { SafeUrl } from '@angular/platform-browser';
import {ImagenService} from '../../../core/services/imagen/imagen.service';

@Component({
  selector: 'app-apartamento-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './apartamento-edit.html'
})
export class ApartamentoEdit implements OnInit {

  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cd = inject(ChangeDetectorRef);
  private imagenService = inject(ImagenService);
  private apartamentoService = inject(ApartamentoService);

  apartamentoId!: number;
  cargando = true;
  guardando = false;
  mensajeError = '';

  subiendoFoto = false;
  imagenSegura: SafeUrl | null = null;
  timestamp: number = Date.now();

  formApartamento: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    direccion: ['', Validators.required],
    ciudad: ['', Validators.required],
    descripcion: ['']
  });

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.apartamentoId = Number(idParam);
      this.cargarDatosApartamento();
    }
  }

  obtenerRutaImagen(apto: any): string | null {
    return this.imagenService.extraerRutaImagen(apto);
  }

  cargarDatosApartamento() {
    this.apartamentoService.getApartamentoById(this.apartamentoId).subscribe({
      next: (apto) => {
        this.formApartamento.patchValue({
          nombre: apto.nombreInterno,
          direccion: apto.direccion,
          ciudad: apto.ciudad,
          descripcion: apto.descripcion
        });

        const rutaImg = this.obtenerRutaImagen(apto);
        if (rutaImg) {
          this.cargarImagenSegura(rutaImg);
        } else {
          this.cargando = false;
          this.cd.detectChanges();
        }
      },
      error: (err) => {
        this.mensajeError = 'Error al cargar los datos del apartamento.';
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  cargarImagenSegura(nombreArchivo: string) {
    this.imagenService.cargarImagenApartamento(nombreArchivo).subscribe({
      next: (url) => {
        this.imagenSegura = url;
        this.cargando = false;
        this.cd.detectChanges();
      },
      error: () => {
        this.cargando = false;
        this.cd.detectChanges();
      }
    });
  }

  subirFoto(event: Event) {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      const archivo = input.files[0];
      this.subiendoFoto = true;
      this.cd.detectChanges();

      this.apartamentoService.subirImagen(this.apartamentoId, archivo).subscribe({
        next: () => {
          this.apartamentoService.getApartamentoById(this.apartamentoId).subscribe(aptoActualizado => {
            const nuevaRuta = this.obtenerRutaImagen(aptoActualizado);
            if (nuevaRuta) {
              this.cargarImagenSegura(nuevaRuta);
            }
            this.timestamp = Date.now();
            this.subiendoFoto = false;
            this.cd.detectChanges();
          });
        },
        error: (err) => {
          console.error('Error al subir la foto', err);
          this.mensajeError = 'Ocurrió un error al subir la foto del apartamento.';
          this.subiendoFoto = false;
          this.cd.detectChanges();
        }
      });
    }
  }

  onSubmit() {
    if (this.formApartamento.invalid) {
      this.formApartamento.markAllAsTouched();
      return;
    }

    this.guardando = true;
    this.cd.detectChanges();

    this.apartamentoService.actualizarApartamento(this.apartamentoId, this.formApartamento.value).subscribe({
      next: () => {
        this.guardando = false;
        void this.router.navigate(['/apartamento', this.apartamentoId]);
      },
      error: (err) => {
        this.mensajeError = 'Error al guardar los cambios.';
        this.guardando = false;
        this.cd.detectChanges();
      }
    });
  }
}
